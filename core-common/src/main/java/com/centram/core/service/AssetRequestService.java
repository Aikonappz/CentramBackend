package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetRequestRepository;
import com.centram.domain.AssetRequest;
import com.centram.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class AssetRequestService {
    private static final Logger log = LoggerFactory.getLogger(AssetRequestService.class);

    @Autowired
    private AssetRequestRepository assetRequestRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MiscService miscService;

    @Transactional(readOnly = true)
    public PaginatedList<AssetRequest> getAssetRequests(Integer productCategory, Integer assetType, String modelNo, String serialNo, Integer approved, Integer allocated, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productCategory = productCategory == null ? -1 : productCategory;
        assetType = assetType == null ? -1 : assetType;
        modelNo = (modelNo == null || modelNo.equalsIgnoreCase("")) ? null : modelNo;
        serialNo = (serialNo == null || serialNo.equalsIgnoreCase("")) ? null : serialNo;
        approved = approved == null ? -1 : approved;
        allocated = allocated == null ? -1 : allocated;
        return new PaginatedList<AssetRequest>(assetRequestRepository.findAll(productCategory, assetType, modelNo, serialNo, approved, allocated, loggedInUser.getUserId(), pageable));
    }

    @Transactional(readOnly = true)
    public AssetRequest getAssetRequest(BigInteger assetId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssetRequest assetRequest = assetRequestRepository.getAsset(loggedInUser.getUserId(), assetId);
        if (assetRequest == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return assetRequest;
    }

    @Transactional(readOnly = false)
    public AssetRequest save(AssetRequest assetRequest) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (assetRequest.getId() == null) {
            assetRequest.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
            assetRequest.setUser(new User(userService.getUserById(loggedInUser.getUserId())));
        }
        assetRequest = assetRequestRepository.save(assetRequest);
        miscService.sendAssetRequestUpdateEmail(assetRequest);
        return assetRequest;
    }
}
