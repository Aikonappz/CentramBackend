package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.LocationRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.Location;
import com.centram.domain.enumarator.ActivityType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * get location
     *
     * @param locationId
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public Location getById(BigInteger locationId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Location> location = locationRepository.findById(locationId);
        if (!location.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return location.get();
    }

    /**
     * get all locations
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Location> getLocations(Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<Location>(locationRepository.getLocationByOrganisation(loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save location
     *
     * @param location
     * @return
     */
    @Transactional
    public Location save(Location location) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        location.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, location.getId() != null ? ActivityType.ADD_LOCATION : ActivityType.UPDATE_LOCATION));
        return locationRepository.save(location);
    }

    /**
     * Update location status
     *
     * @param status
     * @param userIds
     */
    @Transactional
    public void updateLocationsStatus(Status status, List<BigInteger> userIds) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        locationRepository.updateStatus(status, userIds);
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.UPDATE_LOCATION));
    }
}
