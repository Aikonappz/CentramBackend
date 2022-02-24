package com.centram.core.service;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetOrderRepository;
import com.centram.domain.AssetOrder;
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
public class AssetOrderService {
    private static final Logger log = LoggerFactory.getLogger(AssetOrderService.class);

    @Autowired
    private AssetOrderRepository assetOrderRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MiscService miscService;

    @Autowired
    private NotificationService notificationService;

    @Transactional(readOnly = true)
    public PaginatedList<AssetOrder> getOrderedAssets(String orderNo, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderNo = (orderNo.equalsIgnoreCase("")) ? null : orderNo;
        status = (status.equalsIgnoreCase("")) ? null : status;
        return new PaginatedList<AssetOrder>(assetOrderRepository.findAll(orderNo, status, loggedInUser.getOrganisationId(), pageable));
    }

    @Transactional(readOnly = true)
    public AssetOrder getAssetOrderById(BigInteger id) {
        return assetOrderRepository.getById(id);
    }

    @Transactional(readOnly = false)
    public AssetOrder save(AssetOrder assetOrder) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (assetOrder.getId() == null) {
            assetOrder.setOrderNo(orderNo("ORD"));
            assetOrder.setRaisedUser(new User(userService.getUserById(loggedInUser.getUserId())));
            assetOrder.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        }
        assetOrder = assetOrderRepository.save(assetOrder);
        miscService.sendOutBoundAssetUpdateEmail(assetOrder);
        return assetOrder;
    }

    @Transactional(readOnly = false)
    public AssetOrder approveAssetOrder(AssetApprovalDTO assetApprovalDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<AssetOrder> assetOrderOptional = assetOrderRepository.findById(assetApprovalDTO.getId());
        if (assetOrderOptional.isPresent()) {
            AssetOrder assetOrder = assetOrderOptional.get();
            if (assetApprovalDTO.getApproverNo() == 1) {
                assetOrder.setApprovedUser1(assetApprovalDTO.getApproval());
                assetOrder.setApproverUser1Comment(assetApprovalDTO.getFeedback());
                assetOrder = assetOrderRepository.save(assetOrder);
            } else if (assetApprovalDTO.getApproverNo() == 2) {
                assetOrder.setApprovedUser2(assetApprovalDTO.getApproval());
                assetOrder.setApproverUser2Comment(assetApprovalDTO.getFeedback());
                assetOrder = assetOrderRepository.save(assetOrder);
            } else {
                throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
            }
            miscService.sendOutBoundAssetUpdateEmail(assetOrder);
            return assetOrder;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }
}
