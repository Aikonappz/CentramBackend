package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.VendorRepository;

import com.centram.domain.Vendor;
import com.centram.domain.VendorModule;
import com.centram.domain.enumarator.ActivityType;
import com.centram.domain.enumarator.VendorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class VendorService {

    private static final Logger log = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private OrganisationService organisationService;



    /**
     * get vendor
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Vendor getById(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Vendor> optVendor = vendorRepository.findById(id);
        if (!optVendor.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return optVendor.get();
    }

    /**
     * get vendor list by module and submodule wise
     *
     * @param moduleId
     * @param subModuleId
     * @return
     */
    public List<Vendor> getByModuleIdAndSubModuleId(BigInteger moduleId, BigInteger subModuleId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return vendorRepository.getByModuleIdAndSubModuleId(moduleId, subModuleId, loggedInUser.getOrganisationId());
    }

    /**
     * get vendor list by module, submodule and organisation wise
     *
     * @param moduleId
     * @param subModuleId
     * @return
     */
    public List<Vendor> getByModuleIdAndSubModuleId(BigInteger moduleId, BigInteger subModuleId, BigInteger organisationId) {
        return vendorRepository.getByModuleIdAndSubModuleId(moduleId, subModuleId, organisationId);
    }

    /**
     * get vendor by name
     *
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Vendor getByName(String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return vendorRepository.getByName(name, loggedInUser.getOrganisationId());
    }

    @Transactional(readOnly = true)
    public Vendor getByNameAndType(VendorType vendorType, String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return vendorRepository.getByNameAndType(vendorType, name, loggedInUser.getOrganisationId());
    }

    /**
     * get all vendor
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Vendor> getVendors(String inHouse, VendorType vendorType, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean hasFilter = (inHouse.equalsIgnoreCase(""))? false : true;
        Boolean inHouseFilter = false;
        if(hasFilter && inHouse.equals("1")){
            inHouseFilter = true;
        }else if(hasFilter && inHouse.equals("0")){
            inHouseFilter = false;
        }else{
            hasFilter = false;
        }
        return new PaginatedList<Vendor>(vendorRepository.getByOrganisation(hasFilter, inHouseFilter, vendorType, loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * save MapDL
     *
     * @param vendor
     * @return
     */
    @Transactional
    public Vendor save(Vendor vendor) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        vendor.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        List<VendorModule> vendorModules = new ArrayList<VendorModule>();
        for (VendorModule vendorModule : vendor.getVendorModules()) {
            vendorModule.setVendor(vendor);
            vendorModules.add(vendorModule);
        }
        vendor.setVendorModules(vendorModules);

        return vendorRepository.save(vendor);
    }
}