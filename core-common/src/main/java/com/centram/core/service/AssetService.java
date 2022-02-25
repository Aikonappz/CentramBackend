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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.centram.common.utility.Utility.assetNo;

@Service
public class AssetService {
    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    @Value("${app.default.asset.prefix}")
    public String appDefaultAssetPrefix;

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MiscService miscService;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private AssetModelService assetModelService;

    @Autowired
    private AssetRepository assetRepository;

    /**
     * get Assets by organisation and search criteria
     *
     * @param productCategory
     * @param assetType
     * @param modelNo
     * @param serialNo
     * @param assetAvailable
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Asset> getAssets(Integer productCategory, Integer assetType, String modelNo, String serialNo, Integer assetAvailable, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productCategory = productCategory == null ? -1 : productCategory;
        assetType = assetType == null ? -1 : assetType;
        modelNo = (modelNo == null || modelNo.equalsIgnoreCase("")) ? null : modelNo;
        serialNo = (serialNo == null || serialNo.equalsIgnoreCase("")) ? null : serialNo;
        assetAvailable = assetAvailable == null ? -1 : assetAvailable;
        return new PaginatedList<Asset>(assetRepository.findAll(productCategory, assetType, modelNo, serialNo, assetAvailable, loggedInUser.getOrganisationId(), pageable));
    }

    /**
     * get Asset By Id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Asset getAssetById(BigInteger id) {
        Optional<Asset> assetOptional = assetRepository.findById(id);
        if (!assetOptional.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return assetOptional.get();
    }

    /**
     * save an Asset
     *
     * @param asset
     * @return
     */
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

    @Transactional(readOnly = true)
    public ByteArrayInputStream download(Integer productCategory, Integer assetType, String modelNo, String serialNo, Integer assetAvailable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        productCategory = productCategory == null ? -1 : productCategory;
        assetType = assetType == null ? -1 : assetType;
        modelNo = (modelNo == null || modelNo.equalsIgnoreCase("")) ? null : modelNo;
        serialNo = (serialNo == null || serialNo.equalsIgnoreCase("")) ? null : serialNo;
        assetAvailable = assetAvailable == null ? -1 : assetAvailable;

        Page<Asset> page = assetRepository.findAll(
                productCategory,
                assetType,
                modelNo,
                serialNo,
                assetAvailable,
                loggedInUser.getOrganisationId(),
                Pageable.unpaged()
        );
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            data = Arrays.asList(
                    "Product Category",
                    "Asset Type",
                    "Model No.",
                    "Serial No",
                    "Location",
                    "Department Name",
                    "Office Name",
                    "Vendor Name",
                    "Under Warranty?",
                    "Warranty Expired On",
                    "Purchase Type",
                    "Rental Start On",
                    "Rental End On",
                    "Available?"
            );
            csvPrinter.printRecord(data);
            for (Asset asset : page.getContent()) {
                data = Arrays.asList(
                        asset.getProductCategory().name(),
                        asset.getAssetType().name(),
                        asset.getModelNo(),
                        asset.getSerialNo(),
                        asset.getRaisedForLocation().getName(),
                        asset.getDepartment() == null ? "" : asset.getDepartment().getName(),
                        asset.getLocation() == null ? "" : asset.getLocation().getOfficeName(),
                        asset.getVendor().getName(),
                        asset.getIsUnderWarranty() ? "YES" : "NO",
                        asset.getWarrantyExpiredAt().format(DateTimeFormatter.ofPattern(dateFormat)),
                        asset.getPurchaseType().name(),
                        asset.getPurchaseType() == PurchaseType.RENTED ? asset.getRentalStartAt().format(DateTimeFormatter.ofPattern(dateFormat)) : "",
                        asset.getPurchaseType() == PurchaseType.RENTED ? asset.getRentalEndAt().format(DateTimeFormatter.ofPattern(dateFormat)) : "",
                        asset.getIsAvailable() ? "YES" : "NO"
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }
}
