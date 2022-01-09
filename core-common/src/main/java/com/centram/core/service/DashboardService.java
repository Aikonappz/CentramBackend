package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.vo.AdminDashboardVO;
import com.centram.common.vo.OrgAdminDashboardVO;
import com.centram.common.vo.UserDashboardVO;
import com.centram.core.repository.IncidentRepository;
import com.centram.core.repository.OrganisationRepository;
import com.centram.core.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @Transactional(readOnly = true)
    public AdminDashboardVO appAdminDashboard() {
        return organisationRepository.appAdminDashboardData();
    }

    @Transactional(readOnly = true)
    public OrgAdminDashboardVO orgAdminDashboard() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.orgAdminDashboardData(loggedInUser.getOrganisationId());
    }

    @Transactional(readOnly = true)
    public UserDashboardVO userDashboard(LocalDate currentDate) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime endDateTime = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime startDateTime = endDateTime.minusDays(90).toLocalDate().atStartOfDay();
        return incidentRepository.userDashboardData(
                startDateTime,
                endDateTime,
                loggedInUser.getUserId()
        );
    }

}
