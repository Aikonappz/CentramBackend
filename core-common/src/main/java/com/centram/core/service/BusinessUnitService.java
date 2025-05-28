package com.centram.core.service;

import com.centram.common.dto.CommonProjection;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.BusinessUnitRepository;
import com.centram.domain.BusinessUnit;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;

@Service
public class BusinessUnitService {

    @Autowired
    private BusinessUnitRepository businessUnitRepository ;

    @Transactional
    public BusinessUnit save(BusinessUnit BusinessUnit) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return businessUnitRepository.save(BusinessUnit);
    }

    @Transactional
    public BusinessUnit getById(BigInteger id) {
        return businessUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BusinessUnit not found with id: " + id));
    }

    @Transactional
    public PaginatedList<CommonProjection> getAll(String name, Status status, Pageable pageable) {
        return new PaginatedList<CommonProjection>(businessUnitRepository.findAllBy(pageable));
    }
}
