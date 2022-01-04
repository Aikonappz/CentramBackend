package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.DistributionListRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.DistributionList;
import com.centram.domain.DistributionListModule;
import com.centram.domain.enumarator.ActivityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DistributionListService {

    private static final Logger log = LoggerFactory.getLogger(DistributionListService.class);

    @Autowired
    private DistributionListRepository distributionListRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * get MapDL
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public DistributionList getById(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<DistributionList> mapDl = distributionListRepository.findById(id);
        if (!mapDl.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return mapDl.get();
    }

    /**
     * get DL list by module and submodule wise
     *
     * @param moduleId
     * @param subModuleId
     * @return
     */
    public List<DistributionList> getByModuleIdAndSubModuleId(BigInteger moduleId, BigInteger subModuleId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return distributionListRepository.getByModuleIdAndSubModuleId(moduleId, subModuleId, loggedInUser.getOrganisationId());
    }

    /**
     * get DL list by module, submodule and organisation wise
     *
     * @param moduleId
     * @param subModuleId
     * @return
     */
    public List<DistributionList> getByModuleIdAndSubModuleId(BigInteger moduleId, BigInteger subModuleId, BigInteger organisationId) {
        return distributionListRepository.getByModuleIdAndSubModuleId(moduleId, subModuleId, organisationId);
    }

    /**
     * get MapDL by name
     *
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public DistributionList getByName(String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return distributionListRepository.getByName(name, loggedInUser.getOrganisationId());
    }

    /**
     * get all MapDL
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<DistributionList> getDistributionLists(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<DistributionList>(distributionListRepository.getMapDLByOrganisation(loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save MapDL
     *
     * @param distributionList
     * @return
     */
    @Transactional
    public DistributionList save(DistributionList distributionList) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        distributionList.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        Set<DistributionListModule> distributionListModules = new HashSet<DistributionListModule>();
        for (DistributionListModule distributionListModule : distributionList.getDistributionListModules()) {
            distributionListModule.setDistributionList(distributionList);
            distributionListModules.add(distributionListModule);
        }
        distributionList.setDistributionListModules(distributionListModules);
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, distributionList.getId() != null ? ActivityType.ADD_LOCATION : ActivityType.UPDATE_LOCATION));
        return distributionListRepository.save(distributionList);
    }
}
