package com.centram.core.service;


import com.centram.core.repository.LocationRepository;
import com.centram.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;


@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private LocationRepository locationRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "locations", key = "#locationId")
    public Location getById(BigInteger locationId) {
        Optional<Location> location = locationRepository.findById(locationId);
        if (location.isPresent()) {
            return location.get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Page<Location> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }
}
