package com.centram.core.service;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetOrderRepository;
import com.centram.domain.AssetOrder;
import com.centram.domain.Module;
import com.centram.domain.User;
import com.centram.domain.enumarator.PurchaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.centram.common.utility.Utility.orderNo;

@Service
public class AssetOrderService {

    private static final Logger log = LoggerFactory.getLogger(AssetOrderService.class);

    @Value("${app.default.outbound.asset.req.prefix}")
    public String outboundAssetReqPrefix;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MiscService miscService;

    @Autowired
    private AssetOrderRepository assetOrderRepository;

    @Autowired
    private ModuleService moduleService;

    /**
     * get all Ordered Assets
     *
     * @param orderNo
     * @param status
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<AssetOrder> getOrderedAssets(String orderNo, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderNo = (orderNo.equalsIgnoreCase("")) ? null : orderNo;
        status = (status.equalsIgnoreCase("")) ? null : status;
        Page<AssetOrder> assetOrderPage = assetOrderRepository.findAll(orderNo, status, loggedInUser.getOrganisationId(), pageable);
        Module module = null;
        for (int k = 0; k < assetOrderPage.getContent().size(); k++) {
            module = moduleService.getModuleById(assetOrderPage.getContent().get(k).getModuleId());
            assetOrderPage.getContent().get(k).setModuleName(module.getCustomerModuleName());
            assetOrderPage.getContent().get(k).setActualModuleName(module.getName());
            module = moduleService.getModuleById(assetOrderPage.getContent().get(k).getSubModuleId());
            assetOrderPage.getContent().get(k).setSubModuleName(module.getCustomerModuleName());
            assetOrderPage.getContent().get(k).setActualSubModuleName(module.getName());
        }
        return new PaginatedList<AssetOrder>(assetOrderPage);
    }

    /**
     * get Asset Order By Id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public AssetOrder getAssetOrderById(BigInteger id) {
        AssetOrder assetOrder = assetOrderRepository.getById(id);
        Module module = moduleService.getModuleById(assetOrder.getModuleId());
        assetOrder.setModuleName(module.getCustomerModuleName());
        assetOrder.setActualModuleName(module.getName());
        module = moduleService.getModuleById(assetOrder.getSubModuleId());
        assetOrder.setSubModuleName(module.getCustomerModuleName());
        assetOrder.setActualSubModuleName(module.getName());
        return assetOrder;
    }

    /**
     * Save Asset Order
     *
     * @param assetOrder
     * @return
     */
    @Transactional(readOnly = false)
    public AssetOrder save(AssetOrder assetOrder) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (assetOrder.getId() == null) {
            assetOrder.setOrderNo(orderNo(outboundAssetReqPrefix));
            assetOrder.setRaisedUser(new User(userService.getUserById(loggedInUser.getUserId())));
            assetOrder.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
        }
        if (assetOrder.getPurchaseType() == PurchaseType.RENTED) {
            ZonedDateTime rentStartAt = ZonedDateTime.of(assetOrder.getRentStartAt(), ZoneId.of(loggedInUser.getTimeZone()));
            assetOrder.setRentStartAt(rentStartAt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
            ZonedDateTime rentEndAt = ZonedDateTime.of(assetOrder.getRentEndAt().plusHours(23).plusMinutes(59).plusSeconds(59), ZoneId.of(loggedInUser.getTimeZone()));
            assetOrder.setRentEndAt(rentEndAt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (!assetOrder.getExistingAgreement()) {
            ZonedDateTime agreementEndAt = ZonedDateTime.of(assetOrder.getAgreementEndAt().plusHours(23).plusMinutes(59).plusSeconds(59), ZoneId.of(loggedInUser.getTimeZone()));
            assetOrder.setAgreementEndAt(agreementEndAt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        }
        assetOrder = assetOrderRepository.save(assetOrder);
        Module module = moduleService.getModuleById(assetOrder.getModuleId());
        assetOrder.setModuleName(module.getCustomerModuleName());
        assetOrder.setActualModuleName(module.getName());
        module = moduleService.getModuleById(assetOrder.getSubModuleId());
        assetOrder.setSubModuleName(module.getCustomerModuleName());
        assetOrder.setActualSubModuleName(module.getName());
        miscService.sendOutBoundAssetUpdateEmail(assetOrder);
        return assetOrder;
    }

    /**
     * take action on Asset Order
     *
     * @param assetApprovalDTO
     * @return
     */
    @Transactional(readOnly = false)
    public AssetOrder assetOrderAction(AssetApprovalDTO assetApprovalDTO) {
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
            Module module = moduleService.getModuleById(assetOrder.getModuleId());
            assetOrder.setModuleName(module.getCustomerModuleName());
            assetOrder.setActualModuleName(module.getName());
            module = moduleService.getModuleById(assetOrder.getSubModuleId());
            assetOrder.setSubModuleName(module.getCustomerModuleName());
            assetOrder.setActualSubModuleName(module.getName());
            miscService.sendOutBoundAssetUpdateEmail(assetOrder);
            return assetOrder;
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }
}
