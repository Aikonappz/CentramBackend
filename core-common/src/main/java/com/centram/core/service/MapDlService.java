package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.MapDlRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.MapDL;
import com.centram.domain.enumarator.ActivityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class MapDlService {

    private static final Logger log = LoggerFactory.getLogger(MapDlService.class);

    @Autowired
    private MapDlRepository mapDlRepository;

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
    public MapDL getById(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<MapDL> mapDl = mapDlRepository.findById(id);
        if (!mapDl.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return mapDl.get();
    }

    /**
     * get MapDL by name
     *
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public MapDL getByName(String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mapDlRepository.getByName(name, loggedInUser.getOrganisationId());
    }

    /**
     * get all MapDL
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<MapDL> getMapDLs(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<MapDL>(mapDlRepository.getMapDLByOrganisation(loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save MapDL
     *
     * @param mapDL
     * @return
     */
    @Transactional
    public MapDL save(MapDL mapDL) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mapDL.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, mapDL.getId() != null ? ActivityType.ADD_LOCATION : ActivityType.UPDATE_LOCATION));
        return mapDlRepository.save(mapDL);
    }
}
