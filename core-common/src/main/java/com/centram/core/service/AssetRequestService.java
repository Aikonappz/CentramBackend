package com.centram.core.service;


import com.centram.common.dto.AllocateAssetDTO;
import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetRequestRepository;
import com.centram.domain.Asset;
import com.centram.domain.AssetRequest;
import com.centram.domain.User;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

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

    @Autowired
    private MediaService mediaService;

    @Autowired
    private AssetService assetService;

    @Transactional(readOnly = true)
    public PaginatedList<AssetRequest> getAssetRequests(Integer productCategory, Integer assetType, String modelNo, String serialNo, Integer approved, Integer allocated, Integer requestFrom, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productCategory = productCategory == null ? -1 : productCategory;
        assetType = assetType == null ? -1 : assetType;
        modelNo = (modelNo == null || modelNo.equalsIgnoreCase("")) ? null : modelNo;
        serialNo = (serialNo == null || serialNo.equalsIgnoreCase("")) ? null : serialNo;
        approved = approved == null ? -1 : approved;
        allocated = allocated == null ? -1 : allocated;
        requestFrom = requestFrom == null ? -1 : requestFrom;
        return new PaginatedList<AssetRequest>(assetRequestRepository.findAll(productCategory, assetType, modelNo, serialNo, approved, allocated, requestFrom, loggedInUser.getUserId(), pageable));
    }

    @Transactional(readOnly = true)
    public AssetRequest getAssetRequest(BigInteger assetId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AssetRequest assetRequest = assetRequestRepository.getAsset(assetId);
        if (assetRequest == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        assetRequest.setAttachment(mediaService.getMediaFile(EntityType.ASSET_REQUEST, MediaType.ASSET_REQUEST, assetId));
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
        miscService.sendInboundAssetRequestUpdateEmail(assetRequest);
        return assetRequest;
    }

    @Transactional(readOnly = false)
    public AssetRequest approveAssetRequest(AssetApprovalDTO assetApprovalDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<AssetRequest> assetRequestOptional = assetRequestRepository.findById(assetApprovalDTO.getId());
        if (assetRequestOptional.isPresent()) {
            AssetRequest assetRequest = assetRequestOptional.get();
            assetRequest.setApproved(assetApprovalDTO.getApproval());
            assetRequest.setApproverComment(assetApprovalDTO.getFeedback());
            assetRequest = assetRequestRepository.save(assetRequest);
            miscService.sendInboundAssetRequestUpdateEmail(assetRequest);
            return assetRequest;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    @Transactional(readOnly = false)
    public AssetRequest allocateAssetRequest(AllocateAssetDTO allocateAssetDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Asset asset = assetService.getAssetById(allocateAssetDTO.getAssetId());
        if (asset == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        } else {
            asset.setIsAvailable(!allocateAssetDTO.getAllocate());
            asset = assetService.save(asset);
            Optional<AssetRequest> assetRequestOptional = assetRequestRepository.findById(allocateAssetDTO.getRequestId());
            if (assetRequestOptional.isPresent()) {
                AssetRequest assetRequest = assetRequestOptional.get();
                assetRequest.setAsset(allocateAssetDTO.getAllocate() ? asset : null);
                assetRequest.setAllocated(allocateAssetDTO.getAllocate());
                assetRequest.setItTeamComment(allocateAssetDTO.getFeedback());
                assetRequest = assetRequestRepository.save(assetRequest);
                //miscService.sendInboundAssetRequestUpdateEmail(assetRequest);
                return assetRequest;
            } else {
                throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
            }
        }
    }

    @Transactional(readOnly = true)
    public Boolean hasApprovalPermission(LoggedInUser loggedInUser, BigInteger requestId) {
        Optional<AssetRequest> assetRequestOptional = assetRequestRepository.findById(requestId);
        if (assetRequestOptional.isPresent()) {
            AssetRequest assetRequest = assetRequestOptional.get();
            return loggedInUser.getUserId().compareTo(assetRequest.getUser().getManagerId()) == 0;
        }
        return false;
    }
}
