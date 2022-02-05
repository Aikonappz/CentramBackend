package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.vo.*;
import com.centram.core.repository.IncidentRepository;
import com.centram.core.repository.OrganisationRepository;
import com.centram.core.repository.UserRepository;
import com.centram.core.repository.VendorRepository;
import com.centram.domain.Permission;
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
        orgAdminDashboardVO.setIncidents(incidentRepository.orgStatusWiseIncidentDashboardData(
                startDateTime,
                endDateTime,
                false,
                null,
                loggedInUser.getOrganisationId(),
                "ORG_ADMIN",
                null
        ));
        return orgAdminDashboardVO;
    }

    @Transactional(readOnly = true)
    public UserDashboardVO userDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        List<Permission> permissions = permissionService.getPermissionByRoleIds(loggedInUser.getRoles());
        List<BigInteger> userModules = permissions.stream()
                .filter(i -> {
                    return (i.getModule().getAppModule() == false && i.getModule().getParentModuleId() != null && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("9"))) == 0);
                })
                .map(permission -> {
                    return permission.getModule().getParentModuleId();
                })
                .collect(Collectors.toList());
        return new UserDashboardVO(incidentRepository.orgStatusWiseIncidentDashboardData(
                startDateTime,
                endDateTime,
                true,
                userModules,
                loggedInUser.getOrganisationId(),
                "USER",
                loggedInUser.getUserId()
        ));
    }

    @Transactional(readOnly = true)
    public AgentDashboardVO agentDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        List<Permission> permissions = permissionService.getPermissionByRoleIds(loggedInUser.getRoles());
        List<BigInteger> userModules = permissions.stream()
                .filter(i -> {
                    return (i.getModule().getAppModule() == false && i.getModule().getParentModuleId() == null);
                })
                .map(permission -> {
                    return permission.getModule().getId();
                })
                .collect(Collectors.toList());
        List<BigInteger> userSubModules = permissions.stream()
                .filter(i -> {
                    return (i.getModule().getAppModule() == false && i.getModule().getParentModuleId() != null
                            && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("7"))) == 0
                    );
                })
                .map(permission -> {
                    return permission.getModule().getId();
                })
                .collect(Collectors.toList());
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
        agentDashboardVO.setStatusIncidents(
                incidentRepository.orgStatusWiseIncidentDashboardData(
                        startDateTime,
                        endDateTime,
                        true,
                        userModules,
                        loggedInUser.getOrganisationId(),
                        userType,
                        loggedInUser.getUserId()
                )
        );
        agentDashboardVO.setPriorityIncidents(
                incidentRepository.orgPriorityWiseIncidentDashboardData(
                        startDateTime,
                        endDateTime,
                        true,
                        userModules,
                        userSubModules,
                        loggedInUser.getOrganisationId(),
                        userType,
                        true,
                        loggedInUser.getUserId()
                )
        );
        return agentDashboardVO;
    }

    @Transactional(readOnly = true)
    public CategoryAdminDashboardVO categoryAdminDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        List<Permission> permissions = permissionService.getPermissionByRoleIds(loggedInUser.getRoles());
        List<BigInteger> userModules = permissions.stream()
                .filter(i -> {
                    return (i.getModule().getAppModule() == false && i.getModule().getParentModuleId() == null);
                })
                .map(permission -> {
                    return permission.getModule().getId();
                })
                .collect(Collectors.toList());
        List<BigInteger> userSubModules = permissions.stream()
                .filter(i -> {
                    return (i.getModule().getAppModule() == false && i.getModule().getParentModuleId() != null
                            && i.getAction().getId().compareTo(BigInteger.valueOf(Long.valueOf("5"))) == 0
                    );
                })
                .map(permission -> {
                    return permission.getModule().getId();
                })
                .collect(Collectors.toList());
        CategoryAdminDashboardVO categoryAdminDashboardVO = incidentRepository.agingWiseIncidentDashboardData(
                startDateTime,
                endDateTime,
                true,
                userModules,
                userSubModules,
                loggedInUser.getOrganisationId()
        );
        categoryAdminDashboardVO.setStatusIncidents(
                incidentRepository.orgStatusWiseIncidentDashboardData(
                        startDateTime,
                        endDateTime,
                        true,
                        userModules,
                        loggedInUser.getOrganisationId(),
                        "CATEGORY_ADMIN",
                        null
                )
        );
        categoryAdminDashboardVO.setPriorityIncidents(
                incidentRepository.orgPriorityWiseIncidentDashboardData(
                        startDateTime,
                        endDateTime,
                        true,
                        userModules,
                        userSubModules,
                        loggedInUser.getOrganisationId(),
                        "CATEGORY_ADMIN",
                        false,
                        null
                )
        );
        return categoryAdminDashboardVO;
    }

}
