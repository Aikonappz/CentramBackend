package com.centram.core.service;


import com.centram.core.repository.*;
import com.centram.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class ProxyService {
    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private HolidayCalendarRepository holidayCalendarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Transactional(readOnly = false)
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    @Transactional(readOnly = false)
    public Priority savePriority(Priority priority) {
        return priorityRepository.save(priority);
    }

    @Transactional(readOnly = false)
    public HolidayCalendar saveHolidayCalender(HolidayCalendar holidayCalendar) {
        return holidayCalendarRepository.save(holidayCalendar);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public Optional<Department> getDepartment(BigInteger id) {
        return departmentRepository.findById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    @Transactional(readOnly = true)
    public Optional<Location> getLocation(BigInteger id) {
        return locationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(BigInteger id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public TimeSheet saveTimeSheet(TimeSheet timeSheet) {
        return timeSheetRepository.save(timeSheet);
    }
}
