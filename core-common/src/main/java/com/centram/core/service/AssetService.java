package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.AssetRepository;
import com.centram.domain.Asset;
import com.centram.domain.Module;
import com.centram.domain.Setting;
import com.centram.domain.enumarator.PurchaseType;
import org.apache.commons.csv.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private ModuleService moduleService;

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
    public PaginatedList<Asset> getAssets(String productCategory, String assetType, String modelNo, String serialNo, Integer assetAvailable, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger mId = (productCategory == null || productCategory.equals("")) ? null : BigInteger.valueOf(Long.valueOf(productCategory));
        BigInteger smId = (assetType == null || assetType.equals("")) ? null : BigInteger.valueOf(Long.valueOf(assetType));
        modelNo = (modelNo == null || modelNo.equalsIgnoreCase("")) ? null : modelNo;
        serialNo = (serialNo == null || serialNo.equalsIgnoreCase("")) ? null : serialNo;
        assetAvailable = assetAvailable == null ? -1 : assetAvailable;
        Page<Asset> assetPage = assetRepository.findAll(mId, smId, modelNo, serialNo, assetAvailable, loggedInUser.getOrganisationId(), pageable);
        Module module = null;
        for (int i = 0; i < assetPage.getContent().size(); i++) {
            module = moduleService.getModuleById(assetPage.getContent().get(i).getModuleId());
            assetPage.getContent().get(i).setModuleName(module.getAssetOPSName());
            assetPage.getContent().get(i).setActualModuleName(module.getName());
            module = moduleService.getModuleById(assetPage.getContent().get(i).getSubModuleId());
            assetPage.getContent().get(i).setSubModuleName(module.getAssetOPSName());
            assetPage.getContent().get(i).setActualSubModuleName(module.getName());
        }
        return new PaginatedList<Asset>(assetPage);
    }

    @Transactional(readOnly = true)
    public List<Asset> getAssets(BigInteger organisationId) {
        List<Asset> assets = assetRepository.findAll(organisationId);
        Module module = null;
        for (int i = 0; i < assets.size(); i++) {
            module = moduleService.getModuleById(assets.get(i).getModuleId());
            assets.get(i).setModuleName(module.getAssetOPSName());
            assets.get(i).setActualModuleName(module.getName());
            module = moduleService.getModuleById(assets.get(i).getSubModuleId());
            assets.get(i).setSubModuleName(module.getAssetOPSName());
            assets.get(i).setActualSubModuleName(module.getName());
        }
        return assets;
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
        Asset asset = assetOptional.get();
        Module module = moduleService.getModuleById(asset.getModuleId());
        asset.setModuleName(module.getCustomerModuleName());
        asset.setActualModuleName(module.getName());
        module = moduleService.getModuleById(asset.getSubModuleId());
        asset.setSubModuleName(module.getCustomerModuleName());
        asset.setActualSubModuleName(module.getName());
        return asset;
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
                asset.setIsAvailable(true);
                asset.setOrganisation(organisationService.getOrganisationById(loggedInUser.getOrganisationId()));
                Setting setting = organisationService.getOrganisationSettings();
                String prefix = (setting != null && setting.getAssetPrefix() != null) ? setting.getAssetPrefix() : appDefaultAssetPrefix;
                Module module = moduleService.getModuleById(asset.getSubModuleId());
                if (module.getGenerateAssetNo()) {
                    asset.setSerialNo(assetNo(prefix));
                }
            }
            ZonedDateTime warrantyDateTime = ZonedDateTime.of(asset.getWarrantyExpiredAt().plusHours(23).plusMinutes(59).plusSeconds(59), ZoneId.of(loggedInUser.getTimeZone()));
            asset.setWarrantyExpiredAt(warrantyDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
            if (asset.getPurchaseType() == PurchaseType.RENTED) {
                ZonedDateTime rentalDateTime = ZonedDateTime.of(asset.getRentalStartAt(), ZoneId.of(loggedInUser.getTimeZone()));
                asset.setRentalStartAt(rentalDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                rentalDateTime = ZonedDateTime.of(asset.getRentalEndAt().plusHours(23).plusMinutes(59).plusSeconds(59), ZoneId.of(loggedInUser.getTimeZone()));
                asset.setRentalEndAt(rentalDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
            }
            return proxyService.saveAsset(asset);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(GenericErrorCode.ASSET_DATA_EXIST);
        }
    }

    @Transactional(readOnly = false)
    public Asset saveViaBatch(Asset asset) {
        return proxyService.saveAsset(asset);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream download(String productCategory, String assetType, String modelNo, String serialNo, Integer assetAvailable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<String> data = new ArrayList<String>();
        BigInteger mId = productCategory.equals("") ? null : BigInteger.valueOf(Long.valueOf(productCategory));
        BigInteger smId = assetType.equals("") ? null : BigInteger.valueOf(Long.valueOf(assetType));
        modelNo = (modelNo == null || modelNo.equalsIgnoreCase("")) ? null : modelNo;
        serialNo = (serialNo == null || serialNo.equalsIgnoreCase("")) ? null : serialNo;
        assetAvailable = assetAvailable == null ? -1 : assetAvailable;
        Page<Asset> page = assetRepository.findAll(
                mId,
                smId,
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
            Module module = null;
            Module subModule = null;
            for (Asset asset : page.getContent()) {
                module = moduleService.getModuleById(asset.getModuleId());
                subModule = moduleService.getModuleById(asset.getSubModuleId());
                data = Arrays.asList(
                        module.getCustomerModuleName(),
                        subModule.getCustomerModuleName(),
                        asset.getModelNo() == null ? "NA" : asset.getModelNo(),
                        asset.getSerialNo(),
                        asset.getRaisedForLocation().getName(),
                        asset.getDepartment() == null ? "NA" : asset.getDepartment().getName(),
                        asset.getLocation() == null ? "NA" : asset.getLocation().getOfficeName(),
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

    /**
     * upload assets data
     *
     * @param multipartFile
     * @throws IOException
     */
    public void uploadAssetsData(MultipartFile multipartFile) throws IOException {
        LoggedInUser loggedInUserDTO = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (multipartFile.getBytes().length == 0) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        List<Map<String, String>> values = new ArrayList<Map<String, String>>();
        List<String> commonHeaders = Arrays.asList("PRODUCT_CATEGORY", "PRODUCT_SUBCATEGORY", "MODEL", "SERIAL_NO", "DEPARTMENT_NAME", "ORG_NAME", "LOCATION_NAME", "UNDER_WARRANTY", "WARRANTY_VALIDITY", "PURCHASE_TYPE", "RENTAL_START_ON", "RENTAL_ENDS_ON", "VENDOR_DETAILS", "OTHER_DETAILS", "REQUESTED_BY", "APPROVER_1", "APPROVER_2");
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
        ) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                values.add(
                        csvRecord.toMap()
                                .entrySet().stream()
                                .filter(i -> commonHeaders.contains(i.getKey()))
                                .collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()))
                );
            }
            //log.info("Uploaded Users data => {}", values);
            miscService.saveBulkAssetData(values);
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
