package com.centram.core.service;


import com.centram.common.vo.AdminDashboardVO;
import com.centram.core.repository.OrganisationRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Transactional(readOnly = true)
    public AdminDashboardVO appAdminDashboard() {
        return organisationRepository.appAdminDashboardData();
    }

}
