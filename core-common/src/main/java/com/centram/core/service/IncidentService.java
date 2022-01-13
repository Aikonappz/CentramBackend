package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.UserVO;
import com.centram.common.vo.WorkingDay;
import com.centram.core.repository.IncidentRepository;
import com.centram.domain.*;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.centram.common.utility.Utility.incidentNo;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
public class IncidentService {
    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);
    @Value("${app.default.incident.prefix}")
    public String appDefaultIncidentPrefix;
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private MiscService miscService;
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
    public PaginatedList<Incident> getAgentIncidents(
            String incidentNo,
            String moduleId,
            String subModuleId,
            String priorityId,
            String assignedUserId,
            String title,
            String status,
            Pageable pageable
    ) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = loggedInUser.getAuthorities().stream()
                .map(i -> i.getAuthority())
                .collect(Collectors.toList());
        List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
        List<BigInteger> modSubModIds = permissions.stream()
                .filter(i -> !i.getModule().getAppModule())
                .map(i -> i.getModule().getId())
                .collect(Collectors.toList());
        BigInteger uId = (!assignedUserId.equals("")) ? BigInteger.valueOf(Long.valueOf(assignedUserId)) : null;
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncomingIncidents(
                incidentNo, mId, smId, pId, uId, modSubModIds, title, intStatus, pageable
        ));
    }

    /**
     * get all non bocked incidents to process in background
     *
     * @return
     */
    public List<Incident> getNonBlockedIncidents() {
        return this.getIncidentsByStatus(new ArrayList<IncidentStatus>() {{
            add(IncidentStatus.OPEN);
            add(IncidentStatus.ASSIGNED);
            add(IncidentStatus.WORK_IN_PROGRESS);
            add(IncidentStatus.SLA_ABOUT_TO_BREACH);
            add(IncidentStatus.SLA_BREACHED);
        }});
    }

    /**
     * get all open incidents
     *
     * @param organisationId
     * @return
     */
    public List<Incident> getOpenIncidents(BigInteger organisationId) {
        List<Incident> incidents = this.getIncidentsByOrganisationAndStatus(organisationId, new ArrayList<IncidentStatus>() {{
            add(IncidentStatus.OPEN);
        }});
        List<Incident> filterd = new ArrayList<>();
        for (int k = 0; k < incidents.size(); k++) {
            filterd.add(incidents.get(k));
        }
        return filterd;
    }

    /**
     * get incident by status
     *
     * @param statusList
     * @return
     */
    @Transactional(readOnly = true)
    public List<Incident> getIncidentsByStatus(List<IncidentStatus> statusList) {
        return incidentRepository.getIncidentsByStatus(statusList);
    }

    @Transactional(readOnly = true)
    public List<Incident> getIncidentsByOrganisationAndStatus(BigInteger organisationId, List<IncidentStatus> statusList) {
        return incidentRepository.getIncidentsByOrganisationAndStatus(organisationId, statusList);
    }

    /**
     * get user incidents
     *
     * @param incidentNo
     * @param title
     * @param status
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Incident> getUserIncidents(
            String incidentNo,
            String title,
            String status,
            Pageable pageable
    ) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncidents(loggedInUser.getUserId(), incidentNo, title, intStatus, pageable));
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
        incident.get().setModuleName(moduleService.getModuleById(incident.get().getModuleId()).getCustomerModuleName());
        incident.get().setSubModuleName(moduleService.getModuleById(incident.get().getSubModuleId()).getCustomerModuleName());
        return incident.get();
    }

    /**
     * assign incidents to agent
     *
     * @param ids
     * @param userId
     */
    @Transactional(readOnly = false)
    public void assignIncidents(List<BigInteger> ids, BigInteger userId) {
        incidentRepository.assignIncidents(IncidentStatus.ASSIGNED, userId, LocalDateTime.now(), ids);
        Iterable<Incident> incidents = incidentRepository.findAllById(ids);
        for (Incident incident : incidents) {
            miscService.notifyIncidentAssign(new IncidentEmailVO(incident, appLocalDateTimeFormat, null));
        }
    }

    /**
     * save all incidents
     *
     * @param incidents
     * @return
     */
    @Transactional(readOnly = false)
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
            miscService.notifyIncidentAssignViaBatch(new IncidentEmailVO(incident, appLocalDateTimeFormat, null));
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
        for (Incident incident : incidents) {
            incident.setStatus(IncidentStatus.valueOf(status));
            incident.setReOpened(true);
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
        }
        incidents = incidentRepository.saveAll(incidents);
        for (Incident incident : incidents) {
            miscService.notifyIncidentUpdate(new IncidentEmailVO(incident, appLocalDateTimeFormat, "Incident Reopened!"));
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
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        currentDateTime = currentDateTime.withZoneSameInstant(ZoneId.of(loggedInUser.getTimeZone()));
        if (incident.getId() == null) {
            incident.setRaisedUser(new User(userVO));
            incident.setRaisedAt(LocalDateTime.now());
            Setting setting = organisationService.getOrganisationSettings();
            String prefix = (setting != null && setting.getIncidentPrefix() != null) ? setting.getIncidentPrefix() : appDefaultIncidentPrefix;
            incident.setIncidentNo(incidentNo(prefix));
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
        if (incident.getHoldAt() != null
                && incident.getRaisedUser() != null
                && incident.getStatus() == IncidentStatus.NEED_CLARIFICATION
                && loggedInUser.getUserId().compareTo(incident.getRaisedUser().getId()) == 0) {
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
        miscService.notifyIncidentUpdate(new IncidentEmailVO(raisedIncident, appLocalDateTimeFormat, null));
        return raisedIncident;
    }

    /**
     * check whether status is hold status or not
     *
     * @param status
     * @return
     */
    private Boolean checkStatusOnHold(IncidentStatus status) {
        return status == IncidentStatus.CLOSED || status == IncidentStatus.ON_HOLD ||
                status == IncidentStatus.NEED_CLARIFICATION ||
                status == IncidentStatus.PENDING_FROM_VENDOR;
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
                slaDayDateTime = nextWorkingDayOptional.get()
                        .getDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
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
                slaDayDateTime = nextWorkingDayOptional.get()
                        .getDate().atTime(opsStartTime).atZone(ZoneId.of(loggedInUser.getTimeZone()));
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
                return (dateTime.toLocalDateTime().compareTo(workingDay.getStartTime()) == 0 || dateTime.toLocalDateTime().isAfter(workingDay.getStartTime()))
                        &&
                        (dateTime.toLocalDateTime().compareTo(workingDay.getEndTime()) == 0 || dateTime.toLocalDateTime().isBefore(workingDay.getEndTime()));
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
        return holidays.stream()
                .filter(i -> {
                    return (i.getDate().compareTo(date) == 0);
                })
                .findAny().isPresent();
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
        return workingDays
                .stream().filter(
                        i -> {
                            return i.getDate().isAfter(date);
                        }
                ).findFirst();
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
