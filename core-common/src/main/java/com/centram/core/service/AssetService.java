package com.centram.core.service;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetRepository;
import com.centram.domain.Asset;
import com.centram.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

import static com.centram.common.utility.Utility.orderNo;

@Service
public class AssetService {
    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MiscService miscService;

    @Autowired
    private NotificationService notificationService;

    @Transactional(readOnly = true)
    public PaginatedList<Asset> getAssets(String assetNo, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assetNo = (assetNo.equalsIgnoreCase("")) ? null : assetNo;
        status = (status.equalsIgnoreCase("")) ? null : status;
        return new PaginatedList<Asset>(assetRepository.findAll(assetNo, status, loggedInUser.getOrganisationId(), pageable));
    }

    @Transactional(readOnly = true)
    public Asset getAssetById(BigInteger id) {
        return assetRepository.getById(id);
    }

    @Transactional(readOnly = false)
    public Asset save(Asset asset) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (asset.getId() == null) {
            asset.setAssetNo(orderNo("ORD"));
            asset.setRaisedUser(new User(userService.getUserById(loggedInUser.getUserId())));
            asset.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        }
        asset = assetRepository.save(asset);
        //miscService.sendOutBoundAssetUpdateEmail(asset);
        return asset;
    }

    @Transactional(readOnly = false)
    public Asset approveAsset(AssetApprovalDTO assetApprovalDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Asset> assetOptional = assetRepository.findById(assetApprovalDTO.getId());
        if (assetOptional.isPresent()) {
            Asset asset = assetOptional.get();
            if (assetApprovalDTO.getApproverNo() == 1) {
                asset.setApprovedUser1(assetApprovalDTO.getApproval());
                asset.setApproverUser1Comment(assetApprovalDTO.getFeedback());
                asset = assetRepository.save(asset);
            } else if (assetApprovalDTO.getApproverNo() == 2) {
                asset.setApprovedUser2(assetApprovalDTO.getApproval());
                asset.setApproverUser2Comment(assetApprovalDTO.getFeedback());
                asset = assetRepository.save(asset);
            } else {
                throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
            }
            //miscService.sendOutBoundAssetUpdateEmail(assetOrder);
            return asset;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }
}
