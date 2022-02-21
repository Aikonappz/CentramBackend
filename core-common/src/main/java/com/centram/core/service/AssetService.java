package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetRepository;
import com.centram.domain.Asset;
import com.centram.domain.AssetModel;
import com.centram.domain.Setting;
import com.centram.domain.User;
import com.centram.domain.enumarator.PurchaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.centram.common.utility.Utility.assetNo;

@Service
public class AssetService {
    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    @Value("${app.default.asset.prefix}")
    public String appDefaultAssetPrefix;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private AssetModelService assetModelService;

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
    public PaginatedList<Asset> getAssets(String serialNo, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        serialNo = (serialNo.equalsIgnoreCase("")) ? null : serialNo;
        status = (status.equalsIgnoreCase("")) ? null : status;
        return new PaginatedList<Asset>(assetRepository.findAll(serialNo, loggedInUser.getOrganisationId(), pageable));
    }

    @Transactional(readOnly = true)
    public Asset getAssetById(BigInteger id) {
        return assetRepository.getById(id);
    }

    @Transactional(readOnly = false)
    public Asset save(Asset asset) {
        try {
            LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (asset.getId() == null) {
                asset.setRaisedUser(new User(userService.getUserById(loggedInUser.getUserId())));
                asset.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
                Setting setting = organisationService.getOrganisationSettings();
                String prefix = (setting != null && setting.getAssetPrefix() != null) ? setting.getAssetPrefix() : appDefaultAssetPrefix;
                AssetModel assetModel = assetModelService.getAssetModel(asset.getProductCategory(), asset.getAssetType(), asset.getModelNo());
                if (assetModel.getGenerateAssetNo()) {
                    asset.setSerialNo(assetNo(prefix));
                }
                ZonedDateTime warrantyDateTime = ZonedDateTime.of(asset.getWarrantyExpiredAt().plusHours(23).plusMinutes(59).plusSeconds(59), ZoneId.of(loggedInUser.getTimeZone()));
                asset.setWarrantyExpiredAt(warrantyDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                if (asset.getPurchaseType() == PurchaseType.RENTED) {
                    ZonedDateTime rentalDateTime = ZonedDateTime.of(asset.getRentalStartAt(), ZoneId.of(loggedInUser.getTimeZone()));
                    asset.setRentalStartAt(rentalDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                    rentalDateTime = ZonedDateTime.of(asset.getRentalEndAt().plusHours(23).plusMinutes(59).plusSeconds(59), ZoneId.of(loggedInUser.getTimeZone()));
                    asset.setRentalEndAt(rentalDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                }
            }
            return proxyService.saveAsset(asset);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.ASSET_DATA_EXIST);
        }
    }
}
