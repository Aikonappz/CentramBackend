package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.PriorityRepository;
import com.centram.domain.Priority;
import com.centram.domain.enumarator.PriorityType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PriorityService {

    private static final Logger log = LoggerFactory.getLogger(PriorityService.class);

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProxyService proxyService;

    /**
     * get priority
     *
     * @param priorityId
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "departments", key = "#departmentId")
    public Priority getById(BigInteger priorityId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Priority> priority = priorityRepository.findById(priorityId);
        if (!priority.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return priority.get();
    }

    /**
     * get all priorities
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Priority> getPriorities(BigInteger accountId, String priorityType, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new PaginatedList<Priority>(priorityRepository.getPriorityByOrganisation(accountId, PriorityType.valueOf(priorityType), loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save priority
     *
     * @param priority
     * @return
     */
    @Transactional
    public Priority save(BigInteger organisationId, Priority priority) {
        priority.setOrganisation(organisationService.getOrganisationById(organisationId));
        try {
            priority = proxyService.savePriority(priority);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.DATA_EXIST, new HashMap<String, Object>() {{
                put("entity", "Priority");
            }});
        }
        return priority;
    }

    /**
     * Update priority status
     *
     * @param status
     * @param userIds
     */
    @Transactional
    public void updatePrioritiesStatus(Status status, List<BigInteger> userIds) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        priorityRepository.updateStatus(status, userIds);

    }

    @Transactional(readOnly = true)
    public Priority getPriorityByNameAndAccountIdAndOrganisationId(String name, BigInteger accountId, BigInteger organisationId) {
        return priorityRepository.getPriorityByNameAndAccountIdAndOrganisationId(name, accountId, organisationId);
    }
}
