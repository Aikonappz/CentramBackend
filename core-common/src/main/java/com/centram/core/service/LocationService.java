package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.LocationVO;
import com.centram.core.repository.LocationRepository;
import com.centram.domain.Location;
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
    private ProxyService proxyService;

    /**
     * get location
     *
     * @param locationId
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "locations", key = "#locationId")
    public Location getById(BigInteger locationId) {
        Optional<Location> location = locationRepository.findById(locationId);
        if (!location.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return location.get();
    }

    /**
     * @param locationName
     * @return
     */
    @Transactional(readOnly = true)
    public Location getByLocationName(String locationName) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return locationRepository.getByLocationName(locationName, loggedInUser.getOrganisationId());
    }


    @Transactional(readOnly = true)
    public Location getByOfficeName(String officeName) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return locationRepository.getByOfficeName(officeName, loggedInUser.getOrganisationId());
    }

    /**
     * get all locations
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Location> getLocations(BigInteger accountId, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<Location>(locationRepository.getLocationByOrganisation(accountId, loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<LocationVO> getLocations(BigInteger id) {
        log.info("Puling location data for {}.", id);
        return locationRepository.getLocationByOrganisation(id);
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

    }

    private Location convert(Location location, LocationVO locationVO) {
        location.setCity(locationVO.getCity());
        location.setName(locationVO.getName());
        location.setCountry(locationVO.getCountry());
        location.setStatus(Status.valueOf(locationVO.getStatus()));
        location.setState(locationVO.getState());
        location.setTimezone(locationVO.getTimezone());
        location.setOfficeName(locationVO.getOfficeName());
        location.setId(locationVO.getId());
        location.setOpsEndTime(locationVO.getOpsEndTime());
        location.setOpsStartTime(locationVO.getOpsStartTime());
        return location;
    }

    public void saveAll(List<LocationVO> locations, BigInteger id) {
        log.info("Saving location data for {}.", id);
        Optional<Location> optLocation = Optional.empty();
        Location loc = null;
        if (locations.size() > 0) {
            for (LocationVO location : locations) {
                try {
                    if (location.getId() != null) {
                        optLocation = proxyService.getLocation(location.getId());
                        if (optLocation.isPresent()) {
                            loc = this.convert(optLocation.get(), location);
                            log.info("Saving location data {}.", loc);
                            loc = proxyService.saveLocation(loc);
                        } else {
                            loc = this.convert(new Location(), location);
                            loc.setOrganisation(organisationService.getOrganisationById(id));
                            log.info("Saving location data {}.", loc);
                            loc = proxyService.saveLocation(loc);
                        }
                    } else {
                        loc = this.convert(new Location(), location);
                        loc.setOrganisation(organisationService.getOrganisationById(id));
                        log.info("Saving location data {}.", loc);
                        loc = proxyService.saveLocation(loc);
                    }
                } catch (Exception e) {
                    //log.error(e.getStackTrace().toString());
                    //throw e;
                    continue;
                }
            }
        }
    }

}
