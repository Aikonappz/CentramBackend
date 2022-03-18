package com.centram.core.service;


import com.centram.common.dto.AllocateAssetDTO;
import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.UserVO;
import com.centram.common.vo.WorkingDay;
import com.centram.core.repository.IncidentRepository;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.centram.common.utility.Utility.incidentNo;
import static com.centram.common.utility.Utility.orderNo;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
public class IncidentService {
    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);
    @Value("${app.default.incident.prefix}")
    public String appDefaultIncidentPrefix;
    @Value("${app.default.inbound.asset.req.prefix}")
    public String inboundAssetReqPrefix;
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private MiscService miscService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private HolidayCalenderService holidayCalenderService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private AppEmailService appEmailService;

    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;

    @Value("${app.date.time.view.format}")
    private String appDateTimeViewFormat;


    /**
     * get incidents for agent
     *
     * @param incidentNo
     * @param moduleId
     * @param subModuleId
     * @param priorityId
     * @param assignedUserId
     * @param title
     * @param status
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Incident> getAgentIncidents(String incidentType, Integer assigned, Integer deallocated, String serialNo, Integer approved, String incidentNo, String moduleId, String subModuleId, String priorityId, String assignedUserId, String title, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = loggedInUser.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList());
        List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
        List<BigInteger> modSubModIds = permissions.stream().filter(i -> !i.getModule().getAppModule()).map(i -> i.getModule().getId()).collect(Collectors.toList());
        BigInteger uId = (!assignedUserId.equals("")) ? BigInteger.valueOf(Long.valueOf(assignedUserId)) : null;
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        serialNo = (!serialNo.equals("")) ? "%" + serialNo.toUpperCase() + "%" : null;
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncomingIncidents(LicenseType.valueOf(incidentType), approved, serialNo, assigned, deallocated, incidentNo, mId, smId, pId, uId, modSubModIds, title, intStatus, loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * @param moduleId
     * @param subModuleId
     * @param priorityId
     * @param status
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentReport(BigInteger moduleId, BigInteger subModuleId, BigInteger priorityId, String agingFilter, BigInteger raisedUserId, BigInteger assignedUserId, Integer status, Boolean allOpen, Boolean allClosed, Boolean reOpened, LocalDateTime start, LocalDateTime end, Pageable pageable, Boolean viaBatch, List<String> roleNames, BigInteger organisationId) {
        List<String> roles = new ArrayList<String>();
        if (viaBatch) {
            roles = roleNames;
        } else {
            LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            roles = loggedInUser.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList());
            organisationId = loggedInUser.getOrganisationId();
        }
        List<BigInteger> modSubModIds = new ArrayList<BigInteger>();
        Boolean modFilter = true;
        if (roles.contains("ORG_ADMIN") || roles.contains("ORG_INCIDENT_AGENT_LEAD") || roles.contains("ORG_INCIDENT_AGENT_MANAGER")) {
            modFilter = false;
        } else {
            List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
            modSubModIds = permissions.stream().filter(i -> !i.getModule().getAppModule()).map(i -> i.getModule().getId()).collect(Collectors.toList());
            modFilter = true;
        }
        agingFilter = (agingFilter == null || agingFilter.equalsIgnoreCase("")) ? null : agingFilter;
        return new PaginatedList<Incident>(incidentRepository.incidentReport(moduleId, subModuleId, priorityId, raisedUserId, assignedUserId, status, allOpen, allClosed, reOpened, start, end, modFilter, modSubModIds, agingFilter, organisationId, pageable));
    }

    /**
     * @param moduleId
     * @param subModuleId
     * @param priorityId
     * @param status
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentEscalationReport(BigInteger moduleId, BigInteger subModuleId, BigInteger priorityId, Integer status, LocalDateTime start, LocalDateTime end, Pageable pageable, Boolean viaBatch, List<String> roleNames, BigInteger organisationId) {
        List<String> roles = new ArrayList<String>();
        if (viaBatch) {
            roles = roleNames;
        } else {
            LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            roles = loggedInUser.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList());
            organisationId = loggedInUser.getOrganisationId();
        }
        List<BigInteger> modSubModIds = new ArrayList<BigInteger>();
        Boolean modFilter = true;
        if (roles.contains("ORG_ADMIN") || roles.contains("ORG_INCIDENT_AGENT_LEAD") || roles.contains("ORG_INCIDENT_AGENT_MANAGER")) {
            modFilter = false;
        } else {
            List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
            modSubModIds = permissions.stream().filter(i -> !i.getModule().getAppModule()).map(i -> i.getModule().getId()).collect(Collectors.toList());
            modFilter = true;
        }
        return new PaginatedList<Incident>(incidentRepository.incidentEscalationReport(moduleId, subModuleId, priorityId, status, start, end, modFilter, modSubModIds, organisationId, pageable));
    }

    /**
     * @param moduleId
     * @param subModuleId
     * @param priorityId
     * @param status
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Incident> incidentReopenReport(BigInteger moduleId, BigInteger subModuleId, BigInteger priorityId, Integer status, LocalDateTime start, LocalDateTime end, Pageable pageable, Boolean viaBatch, List<String> roleNames, BigInteger organisationId) {
        List<String> roles = new ArrayList<String>();
        if (viaBatch) {
            roles = roleNames;
        } else {
            LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            roles = loggedInUser.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList());
            organisationId = loggedInUser.getOrganisationId();
        }
        List<BigInteger> modSubModIds = new ArrayList<BigInteger>();
        Boolean modFilter = true;
        if (roles.contains("ORG_ADMIN") || roles.contains("ORG_INCIDENT_AGENT_LEAD") || roles.contains("ORG_INCIDENT_AGENT_MANAGER")) {
            modFilter = false;
        } else {
            List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
            modSubModIds = permissions.stream().filter(i -> !i.getModule().getAppModule()).map(i -> i.getModule().getId()).collect(Collectors.toList());
            modFilter = true;
        }
        return new PaginatedList<Incident>(incidentRepository.incidentReopenReport(moduleId, subModuleId, priorityId, status, start, end, modFilter, modSubModIds, organisationId, pageable));
    }

    /**
     * get all non bocked incidents to process in background
     *
     * @return
     */
    public List<Incident> getNonBlockedIncidents() {
        return this.getAllIncidentsByStatus(new ArrayList<IncidentStatus>() {{
            add(IncidentStatus.OPEN);
            add(IncidentStatus.ASSIGNED);
            add(IncidentStatus.WORK_IN_PROGRESS);
            add(IncidentStatus.CLARIFICATION_PROVIDED);
            add(IncidentStatus.SLA_ABOUT_TO_BREACH);
            add(IncidentStatus.SLA_BREACHED);
        }});
    }

    /**
     * get all open incidents by category subcategory and organisation and location
     *
     * @param organisationId
     * @return
     */
    public List<Incident> getOpenIncidents(BigInteger category, BigInteger subCategory, BigInteger locationId, BigInteger organisationId) {
        return incidentRepository.getUserIncidents(category, subCategory, locationId, organisationId, new ArrayList<IncidentStatus>() {{
            add(IncidentStatus.OPEN);
        }});
    }

    /**
     * get incident by status
     *
     * @param statusList
     * @return
     */
    @Transactional(readOnly = true)
    public List<Incident> getAllIncidentsByStatus(List<IncidentStatus> statusList) {
        return incidentRepository.getAllIncidentsByStatus(statusList);
    }

    /**
     * get incident by status
     *
     * @param statusList
     * @return
     */
    @Transactional(readOnly = true)
    public List<Incident> getIncidentsByStatus(BigInteger organisationId, List<IncidentStatus> statusList) {
        return incidentRepository.getIncidentsByStatus(organisationId, statusList);
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidentsByOrganisationAndStatus(BigInteger organisationId, List<IncidentStatus> statusList) {
        return incidentRepository.getIncidentsByOrganisationAndStatus(organisationId, statusList);
    }

    /**
     * get user incidents/asset incidents
     *
     * @param incidentType
     * @param incidentNo
     * @param title
     * @param status
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Incident> getUserIncidents(String incidentType, Integer assigned, Integer deallocated, String serialNo, String incidentNo, String title, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        serialNo = (!serialNo.equals("")) ? "%" + serialNo.toUpperCase() + "%" : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getUserIncidents(LicenseType.valueOf(incidentType), incidentNo, serialNo, title, intStatus, assigned, deallocated, loggedInUser.getUserId(), pageable));
    }

    /**
     * get incident by id
     *
     * @param incidentId
     * @return
     */
    @Transactional(readOnly = true)
    public Incident getIncidentById(BigInteger incidentId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Incident> incident = incidentRepository.findById(incidentId);
        if (!incident.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        TreeSet<IncidentCommunication> descSortedCommunicationSet = new TreeSet<IncidentCommunication>(new Comparator<IncidentCommunication>() {
            @Override
            public int compare(IncidentCommunication ic1, IncidentCommunication ic2) {
                return ic2.getId().compareTo(ic1.getId());
            }
        });
        for (IncidentCommunication incidentCommunication : incident.get().getCommunications()) {
            incidentCommunication.setAttachments(mediaService.getMediaFiles(incidentCommunication.getId(), EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION));
            descSortedCommunicationSet.add(incidentCommunication);
        }
        incident.get().setCommunications(descSortedCommunicationSet);
        Module module = moduleService.getModuleById(incident.get().getModuleId());
        incident.get().setModuleName(module.getCustomerModuleName());
        incident.get().setActualModuleName(module.getName());
        module = moduleService.getModuleById(incident.get().getSubModuleId());
        incident.get().setSubModuleName(module.getCustomerModuleName());
        incident.get().setActualSubModuleName(module.getName());
        incident.get().setOldAssetId(incident.get().getAsset() != null ? incident.get().getAsset().getId() : null);
        return incident.get();
    }

    /**
     * assign incidents to agent
     *
     * @param ids
     * @param userId
     */
    @Transactional(readOnly = false)
    public void assignIncidents(List<BigInteger> ids, BigInteger userId, String comment) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User assignedUser = new User(userService.getUserById(userId));
        Iterable<Incident> incidents = incidentRepository.findAllById(ids);
        Set<IncidentCommunication> incidentCommunications = new HashSet<IncidentCommunication>();
        for (Incident incident : incidents) {
            incidentCommunications = incident.getCommunications();
            incidentCommunications.add(new IncidentCommunication(comment, incident, assignedUser, Collections.emptyList()));
            incident.setCommunications(incidentCommunications);
            incident.setStatus(IncidentStatus.ASSIGNED);
            incident.setAssignedUser(assignedUser);
            incident = incidentRepository.save(incident);
            miscService.notifyIncidentAssign(new IncidentEmailVO(incident, appDateTimeViewFormat, null));
        }
    }

    /**
     * save all incidents
     *
     * @param incidents
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public List<Incident> saveAll(List<Incident> incidents) {
        Iterable<Incident> source = incidentRepository.saveAll(incidents);
        List<Incident> target = new ArrayList<Incident>();
        source.iterator().forEachRemaining(target::add);
        return target;
    }

    /**
     * @param incidents
     * @return
     */
    @Transactional(readOnly = false)
    public Incident update(Incident incidents) {
        return incidentRepository.save(incidents);
    }

    /**
     * assign incident to agent via batch
     *
     * @param incidents
     */
    @Transactional(readOnly = false)
    public void assignIncidentViaBatch(List<Incident> incidents) {
        for (Incident incident : incidents) {
            miscService.notifyIncidentAssignViaBatch(new IncidentEmailVO(incident, appDateTimeViewFormat, null));
        }
    }

    /**
     * change status of an incident
     *
     * @param status
     * @param ids
     */
    @Transactional(readOnly = false)
    public void changeIncidentsStatus(String status, List<BigInteger> ids) {
        incidentRepository.changeStatus(IncidentStatus.valueOf(status), LocalDateTime.now(), ids);
    }

    /**
     * change status of an incident
     *
     * @param status
     * @param ids
     */
    @Transactional(readOnly = false)
    public void reopenIncident(String status, List<BigInteger> ids) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Iterable<Incident> incidents = incidentRepository.findAllById(ids);
        Set<IncidentCommunication> incidentCommunications = new HashSet<IncidentCommunication>();
        for (Incident incident : incidents) {
            incidentCommunications = incident.getCommunications();
            incidentCommunications.add(new IncidentCommunication("Incident Reopened again!", incident, incident.getRaisedUser(), Collections.emptyList()));
            incident.setCommunications(incidentCommunications);
            incident.setStatus(IncidentStatus.valueOf(status));
            incident.setReopenedAt(LocalDateTime.now());
            incident.setReOpened(true);
            incident.setAssignedUser(null);
            incident.setEscalation1At(null);
            incident.setEscalation2At(null);
            //incident.setAssignedUser(null);
            /*fetch location*/
            Location location = locationService.getById(loggedInUser.getLocationId());
            /*fetch priority*/
            Priority priority = priorityService.getById(incident.getPriority().getId());
            /*prepare holiday List*/
            ZonedDateTime raiseDateTime = ZonedDateTime.now();
            List<Holiday> holidays = new ArrayList<Holiday>();
            List<Holiday> currentYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().toString());
            List<Holiday> nextYearHolidays = new ArrayList<Holiday>();
            if (raiseDateTime.getMonth() == Month.DECEMBER) {
                nextYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().plusYears(1).toString());
            }
            holidays = this.mergeHolidays(currentYearHolidays, nextYearHolidays);
            /*prepare holiday List*/
            raiseDateTime = raiseDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone()));
            incident.setSlaAt(this.getSLADateTime(raiseDateTime, priority.getSla(), location.getOpsStartTime(), location.getOpsEndTime(), holidays));
            incident = incidentRepository.save(incident);
            miscService.notifyIncidentUpdate(new IncidentEmailVO(incident, appDateTimeViewFormat, "Incident Reopened again!", true));
        }
    }

    /**
     * asset request action
     *
     * @param assetApprovalDTO
     */
    @Transactional(readOnly = false)
    public void assetApprovalAction(AssetApprovalDTO assetApprovalDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Incident> optionalIncident = incidentRepository.findById(assetApprovalDTO.getId());
        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();
            Set<IncidentCommunication> incidentCommunications = new HashSet<IncidentCommunication>();
            incidentCommunications = incident.getCommunications();
            User manager = new User(userService.getUserById(incident.getRaisedUser().getManagerId()));
            incidentCommunications.add(new IncidentCommunication(assetApprovalDTO.getFeedback(), incident, manager, Collections.emptyList()));
            incident.setCommunications(incidentCommunications);
            incident.setFeedbackProvided(true);
            incident.setAssetApproved(assetApprovalDTO.getApproval());

            /*fetch location*/
            Location location = locationService.getById(loggedInUser.getLocationId());
            /*fetch priority*/
            Priority priority = priorityService.getById(incident.getPriority().getId());
            /*prepare holiday List*/
            ZonedDateTime raiseDateTime = ZonedDateTime.now();
            List<Holiday> holidays = new ArrayList<Holiday>();
            List<Holiday> currentYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().toString());
            List<Holiday> nextYearHolidays = new ArrayList<Holiday>();
            if (raiseDateTime.getMonth() == Month.DECEMBER) {
                nextYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().plusYears(1).toString());
            }
            holidays = this.mergeHolidays(currentYearHolidays, nextYearHolidays);
            /*prepare holiday List*/
            raiseDateTime = raiseDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone()));
            incident.setSlaAt(this.getSLADateTime(raiseDateTime, priority.getSla(), location.getOpsStartTime(), location.getOpsEndTime(), holidays));

            incident = incidentRepository.save(incident);
            miscService.sendInboundAssetRequestActionEmail(new IncidentEmailVO(incident, appDateTimeViewFormat, assetApprovalDTO.getFeedback()));
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * check user has approval permission or not
     *
     * @param loggedInUser
     * @param requestId
     * @return
     */
    @Transactional(readOnly = true)
    public Boolean hasApprovalPermission(LoggedInUser loggedInUser, BigInteger requestId) {
        Optional<Incident> optionalIncident = incidentRepository.findById(requestId);
        if (optionalIncident.isPresent()) {
            Incident incident = optionalIncident.get();
            return loggedInUser.getUserId().compareTo(incident.getRaisedUser().getManagerId()) == 0;
        }
        return false;
    }

    @Transactional
    public Incident deallocateAsset(AllocateAssetDTO allocateAssetDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        Optional<Incident> incidentOptional = incidentRepository.findById(allocateAssetDTO.getRequestId());
        if (incidentOptional.isPresent()) {
            Incident incident = incidentOptional.get();
            if (incident.getAsset() != null) {
                Asset asset = incident.getAsset();
                asset.setIsAvailable(true);
                assetService.save(asset);
                incident.setDeallocated(true);
                incident.setAsset(null);
                incident.getCommunications().add(new IncidentCommunication(allocateAssetDTO.getFeedback(), incident, new User(userVO), null));
            }
            incident = incidentRepository.save(incident);
            miscService.notifyIncidentUpdate(new IncidentEmailVO(incident, appDateTimeViewFormat, null, false, true));
            return incident;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * save incident data
     *
     * @param incident
     * @return
     */
    @Transactional
    public Incident save(Incident incident) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Incident raisedIncident = null;
        Asset asset = null;
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        currentDateTime = currentDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone()));
        incident.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        incident.setWatchList(incident.getWatchList() == null ? Collections.emptyList() : incident.getWatchList());
        if (incident.getId() == null) {
            incident.setRaisedUser(new User(userVO));
            incident.setRaisedAt(LocalDateTime.now());
            Setting setting = null;
            String prefix = null;
            if (incident.getIncidentType() == LicenseType.INCIDENT) {
                setting = organisationService.getOrganisationSettings();
                prefix = (setting != null && setting.getIncidentPrefix() != null) ? setting.getIncidentPrefix() : appDefaultIncidentPrefix;
                incident.setIncidentNo(incidentNo(prefix));
            } else {
                setting = organisationService.getOrganisationSettings();
                prefix = (setting != null && setting.getInboundAssetRequestPrefix() != null) ? setting.getInboundAssetRequestPrefix() : inboundAssetReqPrefix;
                incident.setIncidentNo(orderNo(prefix));
            }
            /*fetch location*/
            Location location = locationService.getById(loggedInUser.getLocationId());
            /*fetch priority*/
            Priority priority = priorityService.getById(incident.getPriority().getId());
            /*prepare holiday List*/
            List<Holiday> holidays = new ArrayList<Holiday>();
            List<Holiday> currentYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().toString());
            List<Holiday> nextYearHolidays = new ArrayList<Holiday>();
            if (currentDateTime.getMonth() == Month.DECEMBER) {
                nextYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().plusYears(1).toString());
            }
            holidays = this.mergeHolidays(currentYearHolidays, nextYearHolidays);
            /*prepare holiday List*/
            incident.setSlaAt(this.getSLADateTime(currentDateTime, priority.getSla(), location.getOpsStartTime(), location.getOpsEndTime(), holidays));
        }
        Set<IncidentCommunication> communicationSet = new HashSet<IncidentCommunication>();
        for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
            if (incidentCommunication.getId() == null) {
                incidentCommunication.setCommunicatedBy(new User(userVO.getVersion(), userVO.getId()));
            }
            incidentCommunication.setIncident(incident);
            communicationSet.add(incidentCommunication);
        }
        incident.setCommunications(communicationSet);
        // mark incident hold and set hold time
        if (incident.getHoldAt() == null && this.checkStatusOnHold(incident.getStatus())) {
            incident.setHoldAt(LocalDateTime.now());
        }
        // calculate SLA after it's free
        if (incident.getHoldAt() != null && incident.getRaisedUser() != null && incident.getStatus() == IncidentStatus.NEED_CLARIFICATION && loggedInUser.getUserId().compareTo(incident.getRaisedUser().getId()) == 0) {
            incident.setStatus(IncidentStatus.CLARIFICATION_PROVIDED);
            /*fetch location*/
            Location location = locationService.getById(loggedInUser.getLocationId());
            /*fetch priority*/
            Priority priority = priorityService.getById(incident.getPriority().getId());
            /*prepare holiday List*/
            List<Holiday> holidays = new ArrayList<Holiday>();
            List<Holiday> currentYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().toString());
            List<Holiday> nextYearHolidays = new ArrayList<Holiday>();
            if (currentDateTime.getMonth() == Month.DECEMBER) {
                nextYearHolidays = holidayCalenderService.getHolidaysByYear(Year.now().plusYears(1).toString());
            }
            holidays = this.mergeHolidays(currentYearHolidays, nextYearHolidays);
            ZonedDateTime holdDateTime = incident.getHoldAt().atZone(ZoneId.systemDefault());
            holdDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone()));
            incident.setSlaAt(this.getHoldSLADateTime(currentDateTime, holdDateTime, priority.getSla(), location.getOpsStartTime(), location.getOpsEndTime(), holidays));
            incident.setHoldAt(null);
        }
        if (incident.getOldAssetId() != null && incident.getAsset() != null && incident.getOldAssetId().compareTo(incident.getAsset().getId()) != 0) {
            asset = assetService.getAssetById(incident.getOldAssetId());
            asset.setIsAvailable(true);
            assetService.save(asset);
        }
        if (incident.getAsset() != null) {
            incident.setAllocated(true);
            incident.setDeallocated(false);
            asset = assetService.getAssetById(incident.getAsset().getId());
            asset.setIsAvailable(false);
            assetService.save(asset);
            incident.setAsset(asset);
        }
        raisedIncident = incidentRepository.save(incident);
        // sorting incident communication via auto increment id
        TreeSet<IncidentCommunication> descSortedCommunicationSet = new TreeSet<IncidentCommunication>(new Comparator<IncidentCommunication>() {
            @Override
            public int compare(IncidentCommunication ic1, IncidentCommunication ic2) {
                return ic2.getId().compareTo(ic1.getId());
            }
        });
        for (IncidentCommunication incidentCommunication : raisedIncident.getCommunications()) {
            incidentCommunication.setAttachments(mediaService.getMediaFiles(incidentCommunication.getId(), EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION));
            descSortedCommunicationSet.add(incidentCommunication);
        }
        raisedIncident.setCommunications(descSortedCommunicationSet);
        //notify respected user
        miscService.notifyIncidentUpdate(new IncidentEmailVO(raisedIncident, appDateTimeViewFormat, null));
        return raisedIncident;
    }

    /**
     * check whether status is hold status or not
     *
     * @param status
     * @return
     */
    private Boolean checkStatusOnHold(IncidentStatus status) {
        return status == IncidentStatus.CLOSED || status == IncidentStatus.ON_HOLD || status == IncidentStatus.NEED_CLARIFICATION || status == IncidentStatus.PENDING_FROM_VENDOR;
    }

    /**
     * calculate hold incident further SLA
     *
     * @param raisedDateTime
     * @param holdAtDateTime
     * @param hour
     * @param opsStartTime
     * @param opsEndTime
     * @param holidays
     * @return
     */
    private LocalDateTime getHoldSLADateTime(ZonedDateTime raisedDateTime, ZonedDateTime holdAtDateTime, String hour, LocalTime opsStartTime, LocalTime opsEndTime, List<Holiday> holidays) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ChronoUnit chronoUnit = ChronoUnit.MINUTES;
        Long timePassed = chronoUnit.between(holdAtDateTime, raisedDateTime);
        Long hours = Long.valueOf(hour.split((":"))[0]);
        Long minutes = Long.valueOf(hour.split((":"))[1]);
        log.info("hours => {}, minutes => {}", hours, minutes);
        Long minuteToAdd = (hours * 60) + minutes;
        minuteToAdd = minuteToAdd - timePassed;
        log.info(" converted minutes => {}", minuteToAdd);
        List<WorkingDay> workingDays = this.getWorkingDays(raisedDateTime, holidays, opsStartTime, opsEndTime);
        Collections.sort(workingDays);
        ZonedDateTime slaDayDateTime = raisedDateTime;
        ZonedDateTime slaDayEndTime = null;
        Duration duration = null;
        if (this.isHoliday(raisedDateTime.toLocalDate(), holidays)) {
            Optional<WorkingDay> nextWorkingDayOptional = this.nextWorkingDay(raisedDateTime.toLocalDate(), workingDays);
            if (nextWorkingDayOptional.isPresent()) {
                slaDayDateTime = nextWorkingDayOptional.get().getDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
            } else {
                throw new AppException(GenericErrorCode.HOLIDAY_CALENDER_MASTER_DATA_MISSING);
            }
        }
        for (WorkingDay workingDay : workingDays) {
            if (slaDayDateTime.toLocalDate().compareTo(workingDay.getDate()) == 0) {
                slaDayDateTime = slaDayDateTime.plusMinutes(minuteToAdd).toLocalDateTime().atZone(ZoneId.of(loggedInUser.getTimeZone()));
                if (!this.checkIsValidSLA(slaDayDateTime, workingDays)) {
                    slaDayEndTime = slaDayDateTime.with(opsEndTime);
                    duration = Duration.between(slaDayEndTime, slaDayDateTime);
                    minuteToAdd = duration.toMinutes();
                    slaDayDateTime = slaDayDateTime.plusDays(1).toLocalDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
                }
            }
        }
        log.info("UTC {}", slaDayDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        log.info("LOCAL {}", slaDayDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone())).toLocalDateTime());
        return slaDayDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * calculate sla
     *
     * @param raisedDateTime
     * @param hour
     * @param opsStartTime
     * @param opsEndTime
     * @param holidays
     * @return
     */
    private LocalDateTime getSLADateTime(ZonedDateTime raisedDateTime, String hour, LocalTime opsStartTime, LocalTime opsEndTime, List<Holiday> holidays) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long hours = Long.valueOf(hour.split((":"))[0]);
        Long minutes = Long.valueOf(hour.split((":"))[1]);
        log.info("hours => {}, minutes => {}", hours, minutes);
        Long minuteToAdd = (hours * 60) + minutes;
        log.info(" converted minutes => {}", minuteToAdd);
        List<WorkingDay> workingDays = this.getWorkingDays(raisedDateTime, holidays, opsStartTime, opsEndTime);
        Collections.sort(workingDays);
        ZonedDateTime slaDayDateTime = raisedDateTime;
        ZonedDateTime slaDayEndTime = null;
        Duration duration = null;
        if (this.isHoliday(raisedDateTime.toLocalDate(), holidays)) {
            Optional<WorkingDay> nextWorkingDayOptional = this.nextWorkingDay(raisedDateTime.toLocalDate(), workingDays);
            if (nextWorkingDayOptional.isPresent()) {
                slaDayDateTime = nextWorkingDayOptional.get().getDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
            } else {
                throw new AppException(GenericErrorCode.HOLIDAY_CALENDER_MASTER_DATA_MISSING);
            }
        }
        for (WorkingDay workingDay : workingDays) {
            if (slaDayDateTime.toLocalDate().compareTo(workingDay.getDate()) == 0) {
                slaDayDateTime = slaDayDateTime.plusMinutes(minuteToAdd).toLocalDateTime().atZone(ZoneId.of(loggedInUser.getTimeZone()));
                if (!this.checkIsValidSLA(slaDayDateTime, workingDays)) {
                    slaDayEndTime = slaDayDateTime.with(opsEndTime);
                    duration = Duration.between(slaDayEndTime, slaDayDateTime);
                    minuteToAdd = duration.toMinutes();
                    slaDayDateTime = slaDayDateTime.plusDays(1).toLocalDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
                }
            }
        }
        log.info("UTC {}", slaDayDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        log.info("LOCAL {}", slaDayDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone())).toLocalDateTime());
        return slaDayDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * check whether a datetime is working day and fall under shift time
     *
     * @param dateTime
     * @param workingDays
     * @return
     */
    private Boolean checkIsValidSLA(ZonedDateTime dateTime, List<WorkingDay> workingDays) {
        LocalDate date = dateTime.toLocalDate();
        for (WorkingDay workingDay : workingDays) {
            if (workingDay.getDate().compareTo(date) == 0) {
                return (dateTime.toLocalDateTime().compareTo(workingDay.getStartTime()) == 0 || dateTime.toLocalDateTime().isAfter(workingDay.getStartTime())) && (dateTime.toLocalDateTime().compareTo(workingDay.getEndTime()) == 0 || dateTime.toLocalDateTime().isBefore(workingDay.getEndTime()));
            }
        }
        return false;
    }


    /**
     * Check a date is holiday or not
     *
     * @param date
     * @param holidays
     * @return
     */
    private Boolean isHoliday(LocalDate date, List<Holiday> holidays) {
        return holidays.stream().filter(i -> {
            return (i.getDate().compareTo(date) == 0);
        }).findAny().isPresent();
    }

    /**
     * Get list of working days
     *
     * @param dateTime
     * @param holidays
     * @param opsStartTime
     * @param opsEndTime
     * @return
     */
    private List<WorkingDay> getWorkingDays(ZonedDateTime dateTime, List<Holiday> holidays, LocalTime opsStartTime, LocalTime opsEndTime) {
        List<WorkingDay> workingDays = new ArrayList<WorkingDay>();
        LocalDate startDate = dateTime.toLocalDate().with(firstDayOfYear());
        LocalDate endDate = dateTime.toLocalDate().with(lastDayOfYear());
        while (!startDate.isAfter(endDate)) {
            if (!this.isHoliday(startDate, holidays)) {
                workingDays.add(new WorkingDay(startDate, opsStartTime, opsEndTime));
            }
            startDate = startDate.plusDays(1);
        }
        return workingDays;
    }

    /**
     * find next working days from working days list
     *
     * @param date
     * @param workingDays
     * @return
     */
    private Optional<WorkingDay> nextWorkingDay(LocalDate date, List<WorkingDay> workingDays) {
        return workingDays.stream().filter(i -> {
            return i.getDate().isAfter(date);
        }).findFirst();
    }

    /**
     * merge current year and net year holidays into a list
     *
     * @param currentYearHolidays
     * @param nextYearHolidays
     * @return
     */
    private List<Holiday> mergeHolidays(List<Holiday> currentYearHolidays, List<Holiday> nextYearHolidays) {
        List<Holiday> upcomingHolidays = new ArrayList<Holiday>();
        upcomingHolidays.addAll(currentYearHolidays);
        upcomingHolidays.addAll(nextYearHolidays);
        Collections.sort(upcomingHolidays);
        return upcomingHolidays;
    }
}
