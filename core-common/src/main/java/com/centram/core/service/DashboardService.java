package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.vo.*;
import com.centram.core.repository.*;
import com.centram.domain.Permission;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import com.centram.domain.enumarator.LicenseType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ProjectUat> projectUats = projectUatRepository.uatDashboard(startDateTime, endDateTime);
        if (roleNames.contains("ORG_UAT_CONSULTANT")) {
            projectUats = projectUats.stream().filter(i -> {
                return i.getProject().getConsultants().contains(loggedInUser.getEmail());
            }).collect(Collectors.toList());

        }
        if (roleNames.contains("ORG_PROJECT_STAKEHOLDER")) {
            projectUats = projectUats.stream().filter(i -> {
                return i.getProject().getStakeHolders().contains(loggedInUser.getEmail());
            }).collect(Collectors.toList());
        }
        projectUats.forEach(i -> {
            long noOfUatScript = i.getProjectUatScripts().size();
            long noOfUatScriptCompleted = i.getProjectUatScripts().stream().filter(ProjectUatScript::getUatComplete).count();
            if (i.getUatCycleComplete() && noOfUatScript == noOfUatScriptCompleted) {
                i.setStatus("Completed");
            } else if (noOfUatScriptCompleted == 0) {
                i.setStatus("Not Started");
            } else {
                i.setStatus("In Progress");
            }
        });
        uatDashboardVO.setTotal((long) projectUats.size());
        uatDashboardVO.setCompleted(projectUats.stream().filter(i -> {
            return i.getStatus().equalsIgnoreCase("Completed");
        }).count());
        uatDashboardVO.setNotStarted(projectUats.stream().filter(i -> {
            return i.getStatus().equalsIgnoreCase("Not Started");
        }).count());
        uatDashboardVO.setInProgress(projectUats.stream().filter(i -> {
            return i.getStatus().equalsIgnoreCase("In Progress");
        }).count());
        return uatDashboardVO;
    }

}
