package com.centram.core.service;


import com.centram.core.repository.AssetRepository;
import com.centram.core.repository.HolidayCalenderRepository;
import com.centram.core.repository.PriorityRepository;
import com.centram.domain.Asset;
import com.centram.domain.HolidayCalender;
import com.centram.domain.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProxyService {
    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private HolidayCalenderRepository holidayCalenderRepository;

    @Transactional(readOnly = false)
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    @Transactional(readOnly = false)
    public Priority savePriority(Priority priority) {
        return priorityRepository.save(priority);
    }

    @Transactional(readOnly = false)
    public HolidayCalender saveHolidayCalender(HolidayCalender holidayCalender) {
        return holidayCalenderRepository.save(holidayCalender);
    }
}
