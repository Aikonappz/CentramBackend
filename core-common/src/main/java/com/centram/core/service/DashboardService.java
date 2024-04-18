package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.vo.*;
import com.centram.core.repository.*;
import com.centram.domain.*;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

@Service
public class DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ProjectUatRepository projectUatRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectAllocationDetailRepository projectAllocationDetailRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    /**
     * Site Super Admin Dashboard Data
     *
     * @return
     */
    @Transactional(readOnly = true)
    public AdminDashboardVO appAdminDashboard() {
        return organisationRepository.appAdminDashboardData();
    }

    @Transactional(readOnly = true)
    public OrgAdminDashboardVO orgAdminDashboard() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        OrgAdminDashboardVO orgAdminDashboardVO = vendorRepository.orgAdminVendorDashboardData(loggedInUser.getOrganisationId());
        orgAdminDashboardVO.setActiveEmployees(userRepository.orgAdminUserDashboardData(loggedInUser.getOrganisationId()));
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.INCIDENT) {
            orgAdminDashboardVO.setStatusWiseIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, false, null, loggedInUser.getOrganisationId(), "ORG_ADMIN", null));
            orgAdminDashboardVO.setModuleWiseIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, false, null, loggedInUser.getOrganisationId(), "ORG_ADMIN", null));
        }
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.ASSET) {
            orgAdminDashboardVO.setStatusWiseAssetIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, false, null, loggedInUser.getOrganisationId(), "ORG_ADMIN", null));
            orgAdminDashboardVO.setModuleWiseAssetIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, false, null, loggedInUser.getOrganisationId(), "ORG_ADMIN", null));
        }
        return orgAdminDashboardVO;
    }

    @Transactional(readOnly = true)
    public UserDashboardVO userDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        List<Permission> permissions = permissionService.getPermissionByRoleIds(loggedInUser.getRoles());
        List<BigInteger> userModules = permissions.stream().filter(i -> {
            return (!i.getModule().getAppModule() && i.getModule().getParentModuleId() != null && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("9"))) == 0);
        }).map(permission -> {
            return permission.getModule().getParentModuleId();
        }).collect(Collectors.toList());
        UserDashboardVO userDashboardVO = new UserDashboardVO();
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.INCIDENT) {
            userDashboardVO.setStatusWiseIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "USER", loggedInUser.getUserId()));
            userDashboardVO.setModuleWiseIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "USER", loggedInUser.getUserId()));
        }
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.ASSET) {
            userDashboardVO.setStatusWiseAssetIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "USER", loggedInUser.getUserId()));
            userDashboardVO.setModuleWiseAssetIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "USER", loggedInUser.getUserId()));
        }
        return userDashboardVO;
    }

    @Transactional(readOnly = true)
    public AgentDashboardVO agentDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        List<Permission> permissions = permissionService.getPermissionByRoleIds(loggedInUser.getRoles());
        List<BigInteger> userModules = permissions.stream().filter(i -> {
            return (!i.getModule().getAppModule() && i.getModule().getParentModuleId() == null);
        }).map(permission -> {
            return permission.getModule().getId();
        }).collect(Collectors.toList());
        List<BigInteger> userSubModules = permissions.stream().filter(i -> {
            return (!i.getModule().getAppModule() && i.getModule().getParentModuleId() != null && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("7"))) == 0);
        }).map(permission -> {
            return permission.getModule().getId();
        }).collect(Collectors.toList());
        List<BigInteger> incidentModules = permissions.stream().filter(i -> {
            return (!i.getModule().getAppModule() && i.getModule().getParentModuleId() != null && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("7"))) == 0);
        }).map(permission -> {
            return permission.getModule().getParentModuleId();
        }).collect(Collectors.toList());
        String userType = "AGENT";
        if (permissions.stream().filter(i -> {
            return (i.getRole().getId().compareTo(BigInteger.valueOf(Long.valueOf("5"))) == 0);
        }).count() > 0) {
            userType = "AGENT_LEAD";
        } else if (permissions.stream().filter(i -> {
            return (i.getRole().getId().compareTo(BigInteger.valueOf(Long.valueOf("6"))) == 0);
        }).count() > 0) {
            userType = "AGENT_MANAGER";
        }
        AgentDashboardVO agentDashboardVO = new AgentDashboardVO();
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.INCIDENT) {
            agentDashboardVO.setModuleWiseIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, incidentModules, loggedInUser.getOrganisationId(), userType, loggedInUser.getUserId()));
            agentDashboardVO.setStatusWiseIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, incidentModules, loggedInUser.getOrganisationId(), userType, loggedInUser.getUserId()));
            agentDashboardVO.setPriorityWiseIncidents(incidentRepository.orgPriorityWiseIncidentDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, userModules, userSubModules, loggedInUser.getOrganisationId(), userType, true, loggedInUser.getUserId()));
        }
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.ASSET) {
            agentDashboardVO.setModuleWiseAssetIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, incidentModules, loggedInUser.getOrganisationId(), userType, loggedInUser.getUserId()));
            agentDashboardVO.setStatusWiseAssetIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, incidentModules, loggedInUser.getOrganisationId(), userType, loggedInUser.getUserId()));
            agentDashboardVO.setPriorityWiseAssetIncidents(incidentRepository.orgPriorityWiseIncidentDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, userModules, userSubModules, loggedInUser.getOrganisationId(), userType, true, loggedInUser.getUserId()));
        }
        return agentDashboardVO;
    }

    @Transactional(readOnly = true)
    public CategoryAdminDashboardVO categoryAdminDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        List<Permission> permissions = permissionService.getPermissionByRoleIds(loggedInUser.getRoles());
        List<BigInteger> userModules = permissions.stream().filter(i -> {
            return (!i.getModule().getAppModule() && i.getModule().getParentModuleId() == null);
        }).map(permission -> {
            return permission.getModule().getId();
        }).collect(Collectors.toList());
        List<BigInteger> userSubModules = permissions.stream().filter(i -> {
            return (!i.getModule().getAppModule() && i.getModule().getParentModuleId() != null && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("5"))) == 0);
        }).map(permission -> {
            return permission.getModule().getId();
        }).collect(Collectors.toList());
        CategoryAdminDashboardVO categoryAdminDashboardVO = incidentRepository.agingWiseIncidentDashboardData(startDateTime, endDateTime, true, userModules, userSubModules, loggedInUser.getOrganisationId());
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.INCIDENT) {
            categoryAdminDashboardVO.setModuleWiseIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "CATEGORY_ADMIN", null));
            categoryAdminDashboardVO.setStatusWiseIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "CATEGORY_ADMIN", null));
            categoryAdminDashboardVO.setPriorityWiseIncidents(incidentRepository.orgPriorityWiseIncidentDashboardData(LicenseType.INCIDENT.ordinal(), startDateTime, endDateTime, true, userModules, userSubModules, loggedInUser.getOrganisationId(), "CATEGORY_ADMIN", false, null));
        }
        if (loggedInUser.getLicenseType() == LicenseType.ALL || loggedInUser.getLicenseType() == LicenseType.ASSET) {
            categoryAdminDashboardVO.setModuleWiseAssetIncidents(incidentRepository.moduleWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "CATEGORY_ADMIN", null));
            categoryAdminDashboardVO.setStatusWiseAssetIncidents(incidentRepository.statusWiseIncidentsDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, userModules, loggedInUser.getOrganisationId(), "CATEGORY_ADMIN", null));
            categoryAdminDashboardVO.setPriorityWiseAssetIncidents(incidentRepository.orgPriorityWiseIncidentDashboardData(LicenseType.ASSET.ordinal(), startDateTime, endDateTime, true, userModules, userSubModules, loggedInUser.getOrganisationId(), "CATEGORY_ADMIN", false, null));
        }
        return categoryAdminDashboardVO;
    }


    @Transactional(readOnly = true)
    public UATDashboardVO uatDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        UATDashboardVO uatDashboardVO = new UATDashboardVO();
        List<String> roleNames = roleService.getByIds(loggedInUser.getRoles());
        List<ProjectUat> projectUats = projectUatRepository.uatDashboard(startDateTime, endDateTime, loggedInUser.getOrganisationId());
        if (!roleNames.contains("ORG_ADMIN") && roleNames.contains("ORG_UAT_CONSULTANT")) {
            projectUats = projectUats.stream().filter(i -> {
                return i.getProject().getConsultants().contains(loggedInUser.getEmail());
            }).collect(Collectors.toList());

        }
        if (!roleNames.contains("ORG_ADMIN") && roleNames.contains("ORG_PROJECT_STAKEHOLDER")) {
            projectUats = projectUats.stream().filter(i -> {
                return i.getProject().getStakeHolders().contains(loggedInUser.getEmail());
            }).collect(Collectors.toList());
        }
        if (!roleNames.contains("ORG_ADMIN") && roleNames.contains("ORG_ADMIN_PROJECT")) {
            projectUats = projectUats.stream().filter(i -> {
                return i.getProject().getWatchList().contains(loggedInUser.getEmail());
            }).collect(Collectors.toList());
        }
        if (roleNames.contains("ORG_ADMIN")) {
            /*projectUats = projectUats.stream().filter(i -> {
                return i.getProject().getWatchList().contains(loggedInUser.getEmail());
            }).collect(Collectors.toList());*/
        }
        long noOfUat = 0;
        long noOfUatScriptCompleted = 0;
        long noOfUatScriptInProgress = 0;
        long noOfUatScriptNotStarted = 0;
        int status = Integer.MAX_VALUE;
        for (ProjectUat projectUat : projectUats) {
            noOfUat++;
            status = Integer.MAX_VALUE;
            if (projectUat.getUatCycleComplete()) {
                status = 1;
            } else {
                status = this.getUatCycleStatus(projectUat);
            }
            if (status != Integer.MAX_VALUE) {
                if (status == 1) {
                    noOfUatScriptCompleted++;
                } else if (status == -1) {
                    noOfUatScriptNotStarted++;
                } else {
                    noOfUatScriptInProgress++;
                }
            }
        }
        uatDashboardVO.setTotal(noOfUat);
        uatDashboardVO.setCompleted(noOfUatScriptCompleted);
        uatDashboardVO.setNotStarted(noOfUatScriptNotStarted);
        uatDashboardVO.setInProgress(noOfUatScriptInProgress);
        return uatDashboardVO;
    }

    /**
     * @param projectUat
     * @return
     */
    private Integer getUatCycleStatus(ProjectUat projectUat) {
        for (ProjectUatScript projectUatScript : projectUat.getProjectUatScripts()) {
            if (projectUatScript.getUatComplete()) {
                return 0;
            }
        }
        return -1;
    }

    /**
     *
     * @param start
     * @param end
     * @param projectStart
     * @param projectEnd
     * @return
     */
    private Boolean withinRange(LocalDateTime start, LocalDateTime end, LocalDateTime projectStart, LocalDateTime projectEnd) {
        //return (start.isEqual(projectStart) || start.isAfter(projectStart)) && (end.isEqual(projectStart) || end.isBefore(projectStart));
        return (start.isEqual(projectStart) || start.isAfter(projectStart));
    }

    /**
     *
     * @param start
     * @param end
     * @param projectStart
     * @param projectEnd
     * @return
     */
    private Long noOfworkingDays(LocalDateTime start, LocalDateTime end, LocalDateTime projectStart, LocalDateTime projectEnd){
        LocalDate actualStart = start.toLocalDate();
        LocalDate actualEnd = end.toLocalDate();
        if(projectStart.isAfter(start))
            actualStart = projectStart.toLocalDate();
        if(projectEnd.isBefore(end))
            actualEnd = projectEnd.toLocalDate();
        return DAYS.between(actualStart, actualEnd);
    }

    /**
     *
     * @param location
     * @return
     */
    private Long noOfHours(Location location){
        return HOURS.between(location.getOpsStartTime(), location.getOpsEndTime());
    }

    /**
     *
     * @param perDayHour
     * @param noOfDays
     * @return
     */
    private Long totalHours(Long perDayHour, Long noOfDays){
        return perDayHour*noOfDays;
    }

    /**
     *
     * @param userId
     * @param projectId
     * @param start
     * @param end
     * @param projectStart
     * @param projectEnd
     * @return
     */
    private Long submittedTime(BigInteger userId, BigInteger projectId, LocalDateTime start, LocalDateTime end, LocalDateTime projectStart, LocalDateTime projectEnd){
        List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByUser(userId);
        LocalDate actualStart = start.toLocalDate();
        LocalDate actualEnd = end.toLocalDate();
        if(projectStart.isAfter(start))
            actualStart = projectStart.toLocalDate();
        if(projectEnd.isBefore(end))
            actualEnd = projectEnd.toLocalDate();
        Iterator<Map.Entry<LocalDate, LocalTime>> iterator;
        Map.Entry<LocalDate, LocalTime> entry;
        Long total=0l;
        if(!CollectionUtils.isEmpty(timeSheets)){
            for(TimeSheet timeSheet : timeSheets){
                if(!CollectionUtils.isEmpty(timeSheet.getTimeSheetEntries())){
                    for(TimeSheetEntry timeSheetEntry : timeSheet.getTimeSheetEntries()){
                        if(timeSheetEntry.getProject().getId().compareTo(projectId) == 0){
                            iterator = timeSheetEntry.getTimeEntries().entrySet().iterator();
                            while(iterator.hasNext()){
                                entry = iterator.next();
                                if(!(entry.getKey().isBefore(actualStart) && entry.getKey().isAfter(actualEnd)) && entry.getValue() != null){
                                    total += entry.getValue().getHour();
                                }
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    /**
     *
     * @param currentDate
     * @return
     */
    @Transactional(readOnly = true)
    public List<TimeSheetDashBoardVO> timeSheetDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        Page<User> page = userRepository.getUsers(loggedInUser.getOrganisationId(), null, null, Status.ALL.ordinal(), null, null, Pageable.unpaged());
        List<User> users = page.getContent();
        List<ProjectAllocationDetail> projectAllocationDetails;
        List<TimeSheetDashBoardVO> timeSheetDashBoardVOS = new ArrayList<TimeSheetDashBoardVO>();
        for (User user : users) {
            projectAllocationDetails = projectAllocationDetailRepository.getUserProjects(user.getId(), startDateTime, endDateTime);
            if (!CollectionUtils.isEmpty(projectAllocationDetails)) {
                for (ProjectAllocationDetail projectAllocationDetail : projectAllocationDetails) {
                        timeSheetDashBoardVOS.add(
                                new TimeSheetDashBoardVO(
                                        user.getId(),
                                        user.getFirstName()+" "+user.getLastName(),
                                        user.getLocation().getId(),
                                        user.getLocation().getName(),
                                        totalHours(
                                                noOfHours(user.getLocation()),
                                                noOfworkingDays(
                                                    startDateTime,
                                                    endDateTime,
                                                    projectAllocationDetail.getStartDate(),
                                                    projectAllocationDetail.getEndDate()
                                                )
                                        ),
                                        projectAllocationDetail.getProject().getProjectBillingType(),
                                        projectAllocationDetail.getProject().getId(),
                                        projectAllocationDetail.getProject().getName(),
                                        projectAllocationDetail.getStartDate(),
                                        projectAllocationDetail.getEndDate(),
                                        submittedTime(
                                                user.getId(),
                                                projectAllocationDetail.getProject().getId(),
                                                startDateTime,
                                                endDateTime,
                                                projectAllocationDetail.getStartDate(),
                                                projectAllocationDetail.getEndDate()
                                        )
                                )
                        );
                }
            }
        }
        return timeSheetDashBoardVOS;
    }
}
