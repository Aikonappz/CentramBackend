package com.centram.core.service;


import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.TimeSheetRepository;
import com.centram.domain.Location;
import com.centram.domain.TimeSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class TimeSheetService {

    private static final Logger log = LoggerFactory.getLogger(TimeSheetService.class);

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProxyService proxyService;

    /**
     * @param timeSheet
     * @return
     */
    @Transactional(readOnly = false)
    public TimeSheet save(TimeSheet timeSheet) {
        try {
            return proxyService.saveTimeSheet(timeSheet);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.DATA_EXIST, new HashMap<String, Object>() {{
                put("entity", "Time Sheet");
            }});
        }
    }

    /**
     * @param referenceId
     * @return
     */
    @Transactional(readOnly = true)
    public TimeSheet getTimeSheet(UUID referenceId) {
        TimeSheet timeSheet = timeSheetRepository.findByReferenceId(referenceId);
        if (timeSheet == null) {
            throw new AppException(GenericErrorCode.RELEVANT_DATA_NOT_FOUND, new HashMap<String, Object>() {{
                put("entity", "TimeSheet");
            }});
        }
        return timeSheet;
    }

    /**
     * @param organisationId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<TimeSheet> getTimeSheets(BigInteger organisationId, Pageable pageable) {
        return new PaginatedList<TimeSheet>(timeSheetRepository.getTimeSheetByUser(organisationId, pageable));

    }
}