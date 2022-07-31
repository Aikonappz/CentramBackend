package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.Utility;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.AppConfigRepository;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.PurchaseType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.centram.common.utility.Utility.assetNo;


@Service
public class MiscService {

    private static final Logger log = LoggerFactory.getLogger(MiscService.class);
    @Value("${app.default.asset.prefix}")
    public String appDefaultAssetPrefix;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppEmailService appEmailService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private DistributionListService distributionListService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ProxyService proxyService;
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private AppConfigRepository appConfigRepository;
    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Value("${app.local.date.time.format:yyyy-MM-dd'T'HH:mm}")
    private String appLocalDateTimeFormat;
    @Value("${app.mail.reply.email}")
    private String appReplyToEmail;
    @Value("${app.temp.path}")
    private String appTmpPath;
    @Autowired
    private NotificationService notificationService;

    /**
     * Onboard request
     *
     * @param requestDemoDTO
     * @return
     */
    public CommonResponse requestDemo(RequestDemoDTO requestDemoDTO) {
        CommonResponse commonResponse = null;
        appEmailService.sendOnboardRequestMail(requestDemoDTO, new HashMap<>());
        commonResponse = new CommonResponse(Boolean.TRUE, "Request send successfully");
        return commonResponse;
    }

    /**
     * save bulk uploaded user
     *
     * @param dataList
     */
    @Transactional
    @Async("asyncExecutor")
    public void saveBulkUploadedData(List<Map<String, String>> dataList) {
        LoggedInUser loggedInUserDTO = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> users = new ArrayList<User>();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = new UserVO();
        User user = new User();
        String password = null;
        int rowNo = 2;
        Map<Integer, String> rowWiseIssues = new LinkedHashMap<Integer, String>();
        for (Map<String, String> data : dataList) {
            user = new User();
            if (data.get("FIRST_NAME") == null || data.get("FIRST_NAME").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "First Name Required!");
                continue;
            } else {
                user.setFirstName(data.get("FIRST_NAME").trim());
            }
            if (data.get("LAST_NAME") == null || data.get("LAST_NAME").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "Last Name Required!");
                continue;
            } else {
                user.setLastName(data.get("LAST_NAME").trim());
            }
            if (data.get("EMAIL") == null || data.get("EMAIL").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "User Email Required!");
                continue;
            } else {
                User u = userService.getUserByEmail(data.get("EMAIL").trim());
                if (u != null) {
                    rowWiseIssues.put(rowNo++, "User With Same Email already Exist!");
                    continue;
                }
                user.setEmail(data.get("EMAIL").trim());
            }
            if (data.get("CONTACT_NO") == null || data.get("CONTACT_NO").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "Contact No Required!");
                continue;
            } else {
                user.setContactNo(data.get("CONTACT_NO").trim());
            }
            if (data.get("ROLES") == null || data.get("ROLES").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "Roles Required!");
                continue;
            } else {
                List<Role> roles = roleService.getByDisplayNames(Arrays.asList(data.get("ROLES").trim().toUpperCase().split(",")));
                if (roles != null && roles.size() > 0) {
                    user.setRoles(roles.stream().map(Role::getId).collect(Collectors.toList()));
                } else {
                    rowWiseIssues.put(rowNo++, "Provided Roles are not valid!");
                    continue;
                }
            }
            if (data.get("SEC_CONTACT_NO") == null || data.get("SEC_CONTACT_NO").trim().equals("")) {
                user.setSecContactNo(null);
            } else {
                user.setSecContactNo(data.get("SEC_CONTACT_NO").trim());
            }
            if (data.get("EMP_ID") == null || data.get("EMP_ID").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "Employee Id Required!");
                continue;
            } else {
                User u = userService.getUserByEmployeeId(data.get("EMP_ID").trim());
                if (u != null) {
                    rowWiseIssues.put(rowNo++, "User With Same Employee ID already Exist!");
                    continue;
                }
                user.setEmployeeId(data.get("EMP_ID").trim());
            }
            if (data.get("PROJECT_CODE") == null || data.get("PROJECT_CODE").trim().equals("")) {
                user.setProjectCode(null);
            } else {
                user.setProjectCode(data.get("PROJECT_CODE").trim());
            }
            if (data.get("MANAGER_ID") == null || data.get("MANAGER_ID").trim().equals("")) {
                rowWiseIssues.put(rowNo++, "Manager Id Required!");
                continue;
            } else {
                User u = userService.getUserByEmployeeId(data.get("MANAGER_ID").trim());
                if (u != null) {
                    user.setManagerId(u.getId());
                } else {
                    rowWiseIssues.put(rowNo++, "Manager Id is not valid!");
                    continue;
                }
            }
            if (!loggedInUserDTO.getAppManager()) {
                if (data.get("LOCATION") == null || data.get("LOCATION").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Location Required!");
                    continue;
                } else {
                    Location location = locationService.getByLocationName(data.get("LOCATION").trim().toUpperCase(Locale.ROOT));
                    if (location != null) {
                        user.setLocation(location);
                    } else {
                        rowWiseIssues.put(rowNo++, "Location is not valid!");
                        continue;
                    }
                }
                if (data.get("DEPARTMENT") != null && !data.get("DEPARTMENT").trim().equals("")) {
                    Department department = departmentService.getByDepartmentName(data.get("DEPARTMENT").trim().toUpperCase(Locale.ROOT));
                    if (department != null) {
                        user.setDepartment(department);
                    } else {
                        rowWiseIssues.put(rowNo++, "Department is not valid!");
                        continue;
                    }
                }
                if (data.get("VENDOR") != null && !data.get("VENDOR").trim().equals("")) {
                    Vendor vendor = vendorService.getByName(data.get("VENDOR").trim().toUpperCase(Locale.ROOT));
                    if (vendor != null) {
                        user.setVendor(vendor);
                    } else {
                        rowWiseIssues.put(rowNo++, "Vendor is not valid!");
                        continue;
                    }
                }
            }
            if (loggedInUserDTO.getOrganisationId() != null) {
                user.setOrganisation(organisationService.getOrganisationById(loggedInUserDTO.getOrganisationId()));
            }
            password = Utility.getUniqueString(8);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(Status.ACTIVE);
            user.setId(null);
            users.add(user);
            //prepare uservo object
            userVO = new UserVO(user);
            userVO.setPassword(password);
            userVOS.add(userVO);
            rowNo++;
        }
        log.info("{}", users);
        if (users.size() > 0) {
            Iterable<User> userList = userService.saveUsers(users);
            Map<String, Object> mailValues = new HashMap<>();
            for (User usr : userList) {
                userVO = userVOS.stream().filter(i -> {
                    return i.getEmail().equalsIgnoreCase(usr.getEmail());
                }).findFirst().get();
                if (userVO != null) {
                    userVO.setId(usr.getId());
                    userVO.setVersion(usr.getVersion());
                    mailValues = new HashMap<>();
                    log.info("email : {}, password: {}", userVO.getEmail(), userVO.getPassword());
                    mailValues.put("password", userVO.getPassword());
                    //this.sendOnboardMail(userVO, mailValues);
                }
            }
        }
        if (dataList.size() > 0) {
            Map<String, Object> mailValues = new HashMap<>();
            mailValues.put("upload_tp", "User");
            mailValues.put("to", loggedInUserDTO.getEmail());
            mailValues.put("recipient_name", loggedInUserDTO.getName());
            mailValues.put("user", userService.getUserById(loggedInUserDTO.getUserId()));
            mailValues.put("has_issue", false);
            mailValues.put("mailSubject", "successSubject");
            mailValues.put("mailBody", "successBody");
            if (rowWiseIssues.size() > 0) {
                String filePath = appTmpPath.concat("/user-bulk-upload-issues" + System.currentTimeMillis() + ".csv");
                mailValues.put("file", filePath);
                mailValues.put("has_issue", true);
                mailValues.put("mailSubject", "failureSubject");
                mailValues.put("mailBody", "failureBody");
                this.saveUploadIssueFile(filePath, rowWiseIssues);
            }
            appEmailService.sendUploadResult(mailValues);
        }
    }

    /**
     * save bulk uploaded user
     *
     * @param dataList
     */
    @Async("asyncExecutor")
    public void saveBulkAssetData(List<Map<String, String>> dataList) {
        LoggedInUser loggedInUserDTO = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Asset asset = null;
        int rowNo = 2;
        Module module = null;
        Setting setting = null;
        String prefix = null;
        Department department = null;
        Vendor vendor = null;
        User u = null;
        Location location = null;
        Map<Integer, String> rowWiseIssues = new LinkedHashMap<Integer, String>();
        for (Map<String, String> data : dataList) {
            asset = new Asset();
            try {
                if (data.get("PRODUCT_CATEGORY") == null || data.get("PRODUCT_CATEGORY").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Product Category Required!");
                    continue;
                } else {
                    module = moduleService.getModuleByCustomerModuleName(data.get("PRODUCT_CATEGORY").trim());
                    if (module == null) {
                        rowWiseIssues.put(rowNo++, "Product Category not exist!");
                        continue;
                    } else if (module.getAppModule() || module.getParentModuleId() != null) {
                        rowWiseIssues.put(rowNo++, "Not a valid Product Category!");
                        continue;
                    } else {
                        asset.setModuleId(module.getId());
                    }
                }
                if (data.get("PRODUCT_SUBCATEGORY") == null || data.get("PRODUCT_SUBCATEGORY").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Product Sub Category Required!");
                    continue;
                } else {
                    module = moduleService.getSubModuleByCustomerModuleNameAndParentModuleId(asset.getModuleId(), data.get("PRODUCT_SUBCATEGORY").trim());
                    if (module == null) {
                        rowWiseIssues.put(rowNo++, "Not a valid Product Sub Category!");
                        continue;
                    } else if (module.getParentModuleId().compareTo(asset.getModuleId()) != 0) {
                        rowWiseIssues.put(rowNo++, "Asset Category not mapped with Product Category!");
                        continue;
                    } else {
                        asset.setSubModuleId(module.getId());
                    }
                }
                if (data.get("MODEL") == null || data.get("MODEL").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Asset Model Required!");
                    continue;
                } else {
                    asset.setModelNo(data.get("MODEL").trim());
                }
                if (module.getGenerateAssetNo()) {
                    setting = organisationService.getOrganisationSettings();
                    prefix = (setting != null && setting.getAssetPrefix() != null) ? setting.getAssetPrefix() : appDefaultAssetPrefix;
                    asset.setSerialNo(assetNo(prefix));
                } else {
                    asset.setSerialNo(data.get("SERIAL_NO").trim());
                }
                if (data.get("DEPARTMENT_NAME") != null && !data.get("DEPARTMENT_NAME").trim().equals("")) {
                    department = departmentService.getByDepartmentName(data.get("DEPARTMENT_NAME").trim().toUpperCase(Locale.ROOT));
                    if (department != null) {
                        asset.setIsDepartment(true);
                        asset.setDepartment(department);
                    } else {
                        rowWiseIssues.put(rowNo++, "Department is not valid!");
                        continue;
                    }
                }
                if (data.get("ORG_NAME") != null && !data.get("ORG_NAME").trim().equals("")) {
                    location = locationService.getByOfficeName(data.get("ORG_NAME").trim().toUpperCase(Locale.ROOT));
                    if (location != null) {
                        asset.setIsDepartment(false);
                        asset.setLocation(location);
                    } else {
                        rowWiseIssues.put(rowNo++, "Office name is not valid!");
                        continue;
                    }
                }
                if (asset.getDepartment() == null && asset.getLocation() == null) {
                    rowWiseIssues.put(rowNo++, "Department/Office name both can't be blank!");
                    continue;
                }
                if (data.get("LOCATION_NAME") != null && !data.get("LOCATION_NAME").trim().equals("")) {
                    location = locationService.getByLocationName(data.get("LOCATION_NAME").trim().toUpperCase(Locale.ROOT));
                    if (location != null) {
                        asset.setIsLocation(true);
                        asset.setRaisedForLocation(location);
                    } else {
                        rowWiseIssues.put(rowNo++, "Location is not valid!");
                        continue;
                    }
                } else {
                    asset.setIsLocation(false);
                }
                asset.setIsUnderWarranty(data.get("UNDER_WARRANT") != null && data.get("UNDER_WARRANT").trim().equalsIgnoreCase("YES"));
                asset.setWarrantyExpiredAt(LocalDate.parse(data.get("WARRANTY_VALIDITY"), DateTimeFormatter.ofPattern(dateFormat)).plusDays(1).atStartOfDay().minusSeconds(1));
                if (data.get("PURCHASE_TYPE") == null || data.get("PURCHASE_TYPE").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Purchase Type Required!");
                    continue;
                } else {
                    asset.setPurchaseType(data.get("PURCHASE_TYPE").trim().equalsIgnoreCase("RENTED") ? PurchaseType.RENTED : PurchaseType.OWNED);
                }
                if (asset.getPurchaseType() == PurchaseType.RENTED) {
                    if (data.get("RENTAL_START_ON") == null || data.get("RENTAL_START_ON").trim().equals("")) {
                        rowWiseIssues.put(rowNo++, "Rental Start Required!");
                        continue;
                    } else {
                        asset.setRentalStartAt(LocalDate.parse(data.get("RENTAL_START_ON"), DateTimeFormatter.ofPattern(dateFormat)).atStartOfDay());
                    }
                    if (data.get("RENTAL_ENDS_ON") == null || data.get("RENTAL_ENDS_ON").trim().equals("")) {
                        rowWiseIssues.put(rowNo++, "Rental End Required!");
                        continue;
                    } else {
                        asset.setRentalEndAt(LocalDate.parse(data.get("RENTAL_ENDS_ON"), DateTimeFormatter.ofPattern(dateFormat)).plusDays(1).atStartOfDay().minusSeconds(1));
                    }
                }
                if (data.get("VENDOR_DETAILS") != null && !data.get("VENDOR_DETAILS").trim().equals("")) {
                    vendor = vendorService.getByName(data.get("VENDOR_DETAILS").trim().toUpperCase(Locale.ROOT));
                    if (vendor != null) {
                        asset.setVendor(vendor);
                    } else {
                        rowWiseIssues.put(rowNo++, "Vendor is not valid!");
                        continue;
                    }
                } else {
                    rowWiseIssues.put(rowNo++, "Vendor Required!");
                    continue;
                }
                if (data.get("REQUESTED_BY") == null || data.get("REQUESTED_BY").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Requested By Emp Id. Required!");
                    continue;
                } else {
                    u = userService.getUserByEmployeeId(data.get("REQUESTED_BY").trim());
                    if (u != null) {
                        asset.setOrderRequestedUser(u);
                    } else {
                        rowWiseIssues.put(rowNo++, "Requested By Emp Id. is not valid!");
                        continue;
                    }
                }
                if (data.get("APPROVER_1") == null || data.get("APPROVER_1").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Approver 1 Emp Id. Required!");
                    continue;
                } else {
                    u = userService.getUserByEmployeeId(data.get("APPROVER_1").trim());
                    if (u != null) {
                        asset.setApproverUser1(u);
                    } else {
                        rowWiseIssues.put(rowNo++, "Approver 1 Emp Id. is not valid!");
                        continue;
                    }
                }
                if (data.get("APPROVER_2") == null || data.get("APPROVER_2").trim().equals("")) {
                    rowWiseIssues.put(rowNo++, "Approver 2 Emp Id. Required!");
                    continue;
                } else {
                    u = userService.getUserByEmployeeId(data.get("APPROVER_2").trim());
                    if (u != null) {
                        asset.setApproverUser2(u);
                    } else {
                        rowWiseIssues.put(rowNo++, "Approver 2 Emp Id. is not valid!");
                        continue;
                    }
                }
                if (loggedInUserDTO.getOrganisationId() != null) {
                    asset.setOrganisation(organisationService.getOrganisationById(loggedInUserDTO.getOrganisationId()));
                }
                asset.setIsAvailable(true);
                asset = proxyService.saveAsset(asset);
                log.info("{}", asset);
            } catch (DataIntegrityViolationException e) {
                rowWiseIssues.put(rowNo++, GenericErrorCode.ASSET_DATA_EXIST.getTemplate());
                continue;
            } catch (Exception e) {
                rowWiseIssues.put(rowNo++, e.getMessage());
                continue;
            }
        }
        Map<String, Object> mailValues = new HashMap<>();
        mailValues.put("upload_tp", "Asset");
        mailValues.put("to", loggedInUserDTO.getEmail());
        mailValues.put("recipient_name", loggedInUserDTO.getName());
        mailValues.put("user", userService.getUserById(loggedInUserDTO.getUserId()));
        mailValues.put("has_issue", false);
        mailValues.put("mailSubject", "successSubject");
        mailValues.put("mailBody", "successBody");
        if (rowWiseIssues.size() > 0) {
            String filePath = appTmpPath.concat("/asset-bulk-upload-issues-" + System.currentTimeMillis() + ".csv");
            mailValues.put("file", filePath);
            mailValues.put("has_issue", true);
            mailValues.put("mailSubject", "failureSubject");
            mailValues.put("mailBody", "failureBody");
            this.saveUploadIssueFile(filePath, rowWiseIssues);
        }
        appEmailService.sendUploadResult(mailValues);
    }

    /**
     * prepare csv with error list
     *
     * @param absoluteFilePath
     * @param rowWiseIssues
     */
    private void saveUploadIssueFile(String absoluteFilePath, Map<Integer, String> rowWiseIssues) {
        try (CSVPrinter csvPrinter = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL).print(new File(absoluteFilePath), StandardCharsets.UTF_8)) {
            List<String> data = Arrays.asList("Line Number", "Issue Details");
            csvPrinter.printRecord(data);
            for (Map.Entry<Integer, String> entry : rowWiseIssues.entrySet()) {
                data = Arrays.asList(String.valueOf(entry.getKey()), entry.getValue());
                csvPrinter.printRecord(data);
            }
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.CSV_GENERATION_ISSUE);
        }
    }

    /**
     * notify user incident update via mail and notification
     *
     * @param incidentEmailVO
     */
    @Transactional
    @Async("asyncExecutor")
    public void notifyIncidentUpdate(IncidentEmailVO incidentEmailVO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<DistributionList> distributionLists = new ArrayList<DistributionList>();
        List<String> dlEmails = new ArrayList<String>();
        if (incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            distributionLists = distributionListService.getByModuleIdAndSubModuleId(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId());
            dlEmails = distributionLists.stream().map(DistributionList::getDlEmail).collect(Collectors.toList());
        }
        if (incidentEmailVO.getIncidentType() == LicenseType.INCIDENT) {
            List<UserVO> userVOS = userService.getUsersByRoles(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"));
            if (incidentEmailVO.getUserEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
                if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
                    incidentEmailVO.setMailSubjectKey(getSubjectKeyByStatus(incidentEmailVO));
                    incidentEmailVO.setMailBodyKey("incUpdtCnt");
                    incidentEmailVO.setMailToType("AGENT");
                    incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
                    incidentEmailVO.setCc(new String[]{});
                    incidentEmailVO.setBcc(new String[]{});
                    incidentEmailVO.setReplyTo(appReplyToEmail);
                    incidentEmailVO.setCategory(category);
                    incidentEmailVO.setSubCategory(subCategory);
                    incidentEmailVO.setUserVOS(userVOS);
                    incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
                    incidentEmailVO.populateEscalationMatrices();
                    incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
                    appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                } else {
                    if (dlEmails.size() > 0) {
                        incidentEmailVO.setMailSubjectKey("incNewDlSub");
                        incidentEmailVO.setMailBodyKey("incNewDlCnt");
                        incidentEmailVO.setMailToType("AGENT_DL");
                        incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                        incidentEmailVO.setCc(new String[]{});
                        incidentEmailVO.setBcc(new String[]{});
                        incidentEmailVO.setReplyTo(appReplyToEmail);
                        incidentEmailVO.setCategory(category);
                        incidentEmailVO.setSubCategory(subCategory);
                        incidentEmailVO.setUserVOS(userVOS);
                        incidentEmailVO.setRecipientName("DL");
                        incidentEmailVO.populateEscalationMatrices();
                        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                    }
                }
                if (incidentEmailVO.getNewIncident()) {
                    List<String> userEmails = new ArrayList<String>() {{
                        add(incidentEmailVO.getUserEmail());
                    }};
                    userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
                    List<UserVO> users = userService.getUsersByEmails(userEmails);
                    for (UserVO userVO : users) {
                        incidentEmailVO.setMailSubjectKey("incNewEmpSub");
                        incidentEmailVO.setMailBodyKey("incNewEmpCnt");
                        incidentEmailVO.setMailToType("EMP");
                        incidentEmailVO.setTo(new String[]{userVO.getEmail()});
                        incidentEmailVO.setCc(new String[]{});
                        incidentEmailVO.setBcc(new String[]{});
                        incidentEmailVO.setReplyTo(appReplyToEmail);
                        incidentEmailVO.setCategory(category);
                        incidentEmailVO.setSubCategory(subCategory);
                        incidentEmailVO.setUserVOS(userVOS);
                        incidentEmailVO.setRecipientName(userVO.getFullName());
                        incidentEmailVO.populateEscalationMatrices();
                        incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
                        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                    }
                }
            } else {
                List<String> userEmails = new ArrayList<String>() {{
                    add(incidentEmailVO.getUserEmail());
                }};
                userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
                List<UserVO> users = userService.getUsersByEmails(userEmails);
                for (UserVO userVO : users) {
                    incidentEmailVO.setMailSubjectKey(getSubjectKeyByStatus(incidentEmailVO));
                    incidentEmailVO.setMailBodyKey("incUpdtCnt");
                    incidentEmailVO.setMailToType("EMP");
                    incidentEmailVO.setTo(new String[]{userVO.getEmail()});
                    incidentEmailVO.setCc(new String[]{});
                    incidentEmailVO.setBcc(new String[]{});
                    incidentEmailVO.setReplyTo(appReplyToEmail);
                    incidentEmailVO.setCategory(category);
                    incidentEmailVO.setSubCategory(subCategory);
                    incidentEmailVO.setUserVOS(userVOS);
                    incidentEmailVO.setRecipientName(userVO.getFullName());
                    incidentEmailVO.populateEscalationMatrices();
                    incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
                    appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                }
            }
        } else {
            List<UserVO> userVOS = userService.getUsersByRoles(Arrays.asList("ORG_ASSET_AGENT_LEAD", "ORG_ASSET_AGENT_MANAGER"));
            if (incidentEmailVO.getUserEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
                if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
                    incidentEmailVO.setMailSubjectKey(getSubjectKeyByStatus(incidentEmailVO));
                    incidentEmailVO.setMailBodyKey("astUpdtCnt");
                    incidentEmailVO.setMailToType("AGENT");
                    incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
                    incidentEmailVO.setCc(new String[]{});
                    incidentEmailVO.setBcc(new String[]{});
                    incidentEmailVO.setReplyTo(appReplyToEmail);
                    incidentEmailVO.setCategory(category);
                    incidentEmailVO.setSubCategory(subCategory);
                    incidentEmailVO.setUserVOS(userVOS);
                    incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
                    incidentEmailVO.populateEscalationMatrices();
                    incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
                    appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                } else {
                    Module assetType = modules.stream().filter(i -> {
                        return i.getId().equals(incidentEmailVO.getSubModuleId());
                    }).findFirst().get();
                    if (assetType.getRequireApproval() && !incidentEmailVO.getFeedbackProvided()) {
                        User manager = new User(userService.getUserById(incidentEmailVO.getUserManagerId()));
                        incidentEmailVO.setMailSubjectKey("astMngrSub");
                        incidentEmailVO.setMailBodyKey("astMngrCnt");
                        incidentEmailVO.setMailToType("EMP_MNGR");
                        incidentEmailVO.setTo(new String[]{manager.getEmail()});
                        incidentEmailVO.setCc(new String[]{});
                        incidentEmailVO.setBcc(new String[]{});
                        incidentEmailVO.setReplyTo(appReplyToEmail);
                        incidentEmailVO.setCategory(category);
                        incidentEmailVO.setSubCategory(subCategory);
                        incidentEmailVO.setUserVOS(userVOS);
                        incidentEmailVO.setRecipientName(manager.getFirstName() + " " + manager.getLastName());
                        incidentEmailVO.populateEscalationMatrices();
                        incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, manager, Status.PUSHED, NotificationType.INFO)));
                        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                    } else {
                        if (dlEmails.size() > 0 && incidentEmailVO.getAssetApproved()) {
                            incidentEmailVO.setMailSubjectKey("incNewDlSub");
                            incidentEmailVO.setMailBodyKey("incNewDlCnt");
                            incidentEmailVO.setMailToType("AGENT_DL");
                            incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                            incidentEmailVO.setCc(new String[]{});
                            incidentEmailVO.setBcc(new String[]{});
                            incidentEmailVO.setReplyTo(appReplyToEmail);
                            incidentEmailVO.setCategory(category);
                            incidentEmailVO.setSubCategory(subCategory);
                            incidentEmailVO.setUserVOS(userVOS);
                            incidentEmailVO.setRecipientName("DL");
                            incidentEmailVO.populateEscalationMatrices();
                            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                        }
                    }
                }
                if (incidentEmailVO.getNewIncident()) {
                    List<String> userEmails = new ArrayList<String>() {{
                        add(incidentEmailVO.getUserEmail());
                    }};
                    userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
                    List<UserVO> users = userService.getUsersByEmails(userEmails);
                    for (UserVO userVO : users) {
                        incidentEmailVO.setMailSubjectKey("astEmpSub");
                        incidentEmailVO.setMailBodyKey("astEmpCnt");
                        incidentEmailVO.setMailToType("EMP");
                        incidentEmailVO.setTo(new String[]{userVO.getEmail()});
                        incidentEmailVO.setCc(new String[]{});
                        incidentEmailVO.setBcc(new String[]{});
                        incidentEmailVO.setReplyTo(appReplyToEmail);
                        incidentEmailVO.setCategory(category);
                        incidentEmailVO.setSubCategory(subCategory);
                        incidentEmailVO.setUserVOS(userVOS);
                        incidentEmailVO.setRecipientName(userVO.getFullName());
                        incidentEmailVO.populateEscalationMatrices();
                        incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
                        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                    }
                }
            } else {
                List<String> userEmails = new ArrayList<String>() {{
                    add(incidentEmailVO.getUserEmail());
                }};
                userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
                List<UserVO> users = userService.getUsersByEmails(userEmails);
                for (UserVO userVO : users) {
                    incidentEmailVO.setMailSubjectKey(getSubjectKeyByStatus(incidentEmailVO));
                    incidentEmailVO.setMailBodyKey("astUpdtCnt");
                    incidentEmailVO.setMailToType("EMP");
                    incidentEmailVO.setTo(new String[]{userVO.getEmail()});
                    incidentEmailVO.setCc(new String[]{});
                    incidentEmailVO.setBcc(new String[]{});
                    incidentEmailVO.setReplyTo(appReplyToEmail);
                    incidentEmailVO.setCategory(category);
                    incidentEmailVO.setSubCategory(subCategory);
                    incidentEmailVO.setUserVOS(userVOS);
                    incidentEmailVO.setRecipientName(userVO.getFullName());
                    incidentEmailVO.populateEscalationMatrices();
                    incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
                    appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                }
            }
        }
    }

    private String getSubjectKeyByStatus(IncidentEmailVO incidentEmailVO) {
        String status = incidentEmailVO.getStatus();
        Boolean reopened = incidentEmailVO.getReopened();
        if (incidentEmailVO.getIncidentType() == LicenseType.INCIDENT) {
            if (status.equalsIgnoreCase("WORK_IN_PROGRESS")) {
                return "incWipSub";
            } else if (status.equalsIgnoreCase("NEED_CLARIFICATION")) {
                return "incNdClrfSub";
            } else if (status.equalsIgnoreCase("CLARIFICATION_PROVIDED")) {
                return "incClrfPrvdSub";
            } else if (status.equalsIgnoreCase("ON_HOLD")) {
                return "incHldSub";
            } else if (status.equalsIgnoreCase("PENDING_FROM_VENDOR")) {
                return "incPndg3rdPrtySub";
            } else if (status.equalsIgnoreCase("CLOSED")) {
                return "incClsSub";
            } else if (reopened) {
                return "incRopnSub";
            }
            return "incUpdtSub";
        } else {
            if (status.equalsIgnoreCase("WORK_IN_PROGRESS")) {
                return "astWipSub";
            } else if (status.equalsIgnoreCase("NEED_CLARIFICATION")) {
                return "astNdClrfSub";
            } else if (status.equalsIgnoreCase("CLARIFICATION_PROVIDED")) {
                return "astClrfPrvdSub";
            } else if (status.equalsIgnoreCase("ON_HOLD")) {
                return "astHldSub";
            } else if (status.equalsIgnoreCase("PENDING_FROM_VENDOR")) {
                return "astPndg3rdPrtySub";
            } else if (status.equalsIgnoreCase("CLOSED")) {
                return "astClsSub";
            } else if (reopened) {
                return "astRopnSub";
            }
            return "astUpdtSub";
        }
    }

    /**
     * notify user incident assign via mail and notification
     *
     * @param incidentEmailVO
     */
    @Transactional
    @Async("asyncExecutor")
    public void notifyIncidentAssign(IncidentEmailVO incidentEmailVO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //prepare user email
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        if (incidentEmailVO.getIncidentType() == LicenseType.INCIDENT) {
            List<UserVO> userVOS = userService.getUsersByRoles(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"));
            incidentEmailVO.setMailToType("AGENT");
            //prepare agent email
            incidentEmailVO.setMailSubjectKey("incAgntAsgnSub");
            incidentEmailVO.setMailBodyKey("incAgntAsgnCnt");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
            incidentEmailVO.populateEscalationMatrices();
            incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            List<String> userEmails = new ArrayList<String>() {{
                add(incidentEmailVO.getUserEmail());
            }};
            userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
            List<UserVO> users = userService.getUsersByEmails(userEmails);
            for (UserVO userVO : users) {
                //prepare emp email
                incidentEmailVO.setMailToType("EMP");
                incidentEmailVO.setMailSubjectKey("incEmpAsgnSub");
                incidentEmailVO.setMailBodyKey("incUpdtCnt");
                incidentEmailVO.setDescription("Incident assigned to an agent. Please check below details.");
                incidentEmailVO.setTo(new String[]{userVO.getEmail()});
                incidentEmailVO.setCc(new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName(userVO.getFullName());
                incidentEmailVO.populateEscalationMatrices();
                incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        } else {
            List<UserVO> userVOS = userService.getUsersByRoles(Arrays.asList("ORG_ASSET_AGENT_LEAD", "ORG_ASSET_AGENT_MANAGER"));
            incidentEmailVO.setMailToType("AGENT");
            //prepare agent email
            incidentEmailVO.setMailSubjectKey("astAgntAsgnSub");
            incidentEmailVO.setMailBodyKey("astAgntAsgnCnt");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
            incidentEmailVO.populateEscalationMatrices();
            incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            List<String> userEmails = new ArrayList<String>() {{
                add(incidentEmailVO.getUserEmail());
            }};
            userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
            List<UserVO> users = userService.getUsersByEmails(userEmails);
            for (UserVO userVO : users) {
                //prepare emp email
                incidentEmailVO.setMailToType("EMP");
                incidentEmailVO.setMailSubjectKey("astEmpAsgnSub");
                incidentEmailVO.setMailBodyKey("astUpdtCnt");
                incidentEmailVO.setDescription("Incident assigned to an agent. Please check below details.");
                incidentEmailVO.setTo(new String[]{userVO.getEmail()});
                incidentEmailVO.setCc(new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName(userVO.getFullName());
                incidentEmailVO.populateEscalationMatrices();
                incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
    }

    /**
     * notify user incident assign via mail and notification
     *
     * @param incidentEmailVO
     */
    @Transactional
    @Async("asyncExecutor")
    public void notifyIncidentAssignViaBatch(IncidentEmailVO incidentEmailVO) {
        //prepare user email
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"), incidentEmailVO.getOrganisationId());
        //prepare agent email
        incidentEmailVO.setMailToType("AGENT");
        incidentEmailVO.setMailSubjectKey("incAgntAsgnSub");
        incidentEmailVO.setMailBodyKey("incAgntAsgnCnt");
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
        incidentEmailVO.populateEscalationMatrices();
        incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        //prepare emp email
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
        List<UserVO> users = userService.getUsersByEmailsAndOrganisation(userEmails, incidentEmailVO.getOrganisationId());
        for (UserVO userVO : users) {
            incidentEmailVO.setMailToType("EMP");
            incidentEmailVO.setMailSubjectKey("incEmpAsgnSub");
            incidentEmailVO.setMailBodyKey("incUpdtCnt");
            incidentEmailVO.setDescription("Incident assigned to an agent. Please check below details.");
            incidentEmailVO.setTo(new String[]{userVO.getEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(userVO.getFullName());
            incidentEmailVO.populateEscalationMatrices();
            incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        }

    }

    @Transactional
    @Async("asyncExecutor")
    public void notifyWip50PercentTimePassed(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<DistributionList> distributionLists = new ArrayList<DistributionList>();
        List<String> dlEmails = new ArrayList<String>();
        if (incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId(), incidentEmailVO.getOrganisationId());
            dlEmails = distributionLists.stream().map(DistributionList::getDlEmail).collect(Collectors.toList());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"), incidentEmailVO.getOrganisationId());
        List<Notification> userNotifications = new ArrayList<Notification>();
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        incidentEmailVO.setMailSubjectKey("inc50Sub");
        incidentEmailVO.setMailBodyKey("incUpdtCnt");
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            incidentEmailVO.setMailToType("AGENT");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
            incidentEmailVO.populateEscalationMatrices();
            incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        } else {
            if (dlEmails.size() > 0) {
                incidentEmailVO.setMailToType("AGENT_DL");
                incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                incidentEmailVO.setCc(new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName("DL");
                incidentEmailVO.populateEscalationMatrices();
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
    }

    @Transactional
    @Async("asyncExecutor")
    public void notifyWip75PercentTimePassed(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<DistributionList> distributionLists = new ArrayList<DistributionList>();
        List<String> dlEmails = new ArrayList<String>();
        if (incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId(), incidentEmailVO.getOrganisationId());
            dlEmails = distributionLists.stream().map(DistributionList::getDlEmail).collect(Collectors.toList());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"), incidentEmailVO.getOrganisationId());
        incidentEmailVO.setMailSubjectKey("inc75Sub");
        incidentEmailVO.setMailBodyKey("incUpdtCnt");
        List<Notification> userNotifications = new ArrayList<Notification>();
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            incidentEmailVO.setMailToType("AGENT");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
            incidentEmailVO.populateEscalationMatrices();
            incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO)));
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        } else {
            if (dlEmails.size() > 0) {
                incidentEmailVO.setMailToType("AGENT_DL");
                incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                incidentEmailVO.setCc(new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName("DL");
                incidentEmailVO.populateEscalationMatrices();
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
    }

    @Transactional
    @Async("asyncExecutor")
    public void notifySlaBreached(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<DistributionList> distributionLists = new ArrayList<DistributionList>();
        List<String> dlEmails = new ArrayList<String>();
        if (incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId(), incidentEmailVO.getOrganisationId());
            dlEmails = distributionLists.stream().map(DistributionList::getDlEmail).collect(Collectors.toList());
        }
        UserVO agentManagerUserVO = null;
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA") && incidentEmailVO.getAgentManagerId() != null) {
            agentManagerUserVO = userService.getUserById(incidentEmailVO.getAgentManagerId());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"), incidentEmailVO.getOrganisationId());
        if (agentManagerUserVO != null) {
            userVOS.add(agentManagerUserVO);
        }
        Set<String> userEmails = new HashSet<String>();
        Set<UserVO> userVOSet = new HashSet<UserVO>();
        for (UserVO userVO : userVOS) {
            List<String> roles = userVO.getRoleNames();
            for (String s : roles) {
                if (userVO.getEmail().equals(incidentEmailVO.getAgentEmail()) || s.equals("ORG_INCIDENT_AGENT_LEAD") || (incidentEmailVO.getAgentManagerId() != null && userVO.getId().equals(incidentEmailVO.getAgentManagerId()))) {
                    userEmails.add(userVO.getEmail());
                    userVOSet.add(userVO);
                }
            }
        }
        incidentEmailVO.setMailSubjectKey("incSlaBrchdSub");
        incidentEmailVO.setMailBodyKey("incUpdtCnt");
        List<Notification> userNotifications = new ArrayList<Notification>();
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            incidentEmailVO.setMailToType("AGENT");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(userEmails.toArray(new String[0]));
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
            incidentEmailVO.populateEscalationMatrices();
            // prepare notifications list
            //agent notification
            userNotifications.add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
            //agent manager/lead notification
            for (UserVO userVO : userVOSet) {
                userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
            incidentEmailVO.setNotifications(userNotifications);
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        } else {
            if (dlEmails.size() > 0) {
                incidentEmailVO.setMailToType("AGENT_DL");
                incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                incidentEmailVO.setCc(userEmails.toArray(new String[0]));
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName("DL");
                incidentEmailVO.populateEscalationMatrices();
                for (UserVO userVO : userVOSet) {
                    userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
                }
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
    }

    @Transactional
    @Async("asyncExecutor")
    public void notifySlaBreached60MinutesPassed(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<DistributionList> distributionLists = new ArrayList<DistributionList>();
        List<String> dlEmails = new ArrayList<String>();
        if (incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId(), incidentEmailVO.getOrganisationId());
            dlEmails = distributionLists.stream().map(DistributionList::getDlEmail).collect(Collectors.toList());
        }
        UserVO agentManagerUserVO = null;
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA") && incidentEmailVO.getAgentManagerId() != null) {
            agentManagerUserVO = userService.getUserById(incidentEmailVO.getAgentManagerId());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"), incidentEmailVO.getOrganisationId());
        if (agentManagerUserVO != null) {
            userVOS.add(agentManagerUserVO);
        }
        Set<String> userEmails = new HashSet<String>();
        Set<UserVO> userVOSet = new HashSet<UserVO>();
        for (UserVO userVO : userVOS) {
            List<String> roles = userVO.getRoleNames();
            for (String s : roles) {
                if (userVO.getEmail().equals(incidentEmailVO.getAgentEmail()) || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD") || (incidentEmailVO.getAgentManagerId() != null && userVO.getId().equals(incidentEmailVO.getAgentManagerId()))) {
                    userEmails.add(userVO.getEmail());
                    userVOSet.add(userVO);
                }
            }
        }
        incidentEmailVO.setMailSubjectKey("incSlaBrchd60Sub");
        incidentEmailVO.setMailBodyKey("incUpdtCnt");
        List<Notification> userNotifications = new ArrayList<Notification>();
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            incidentEmailVO.setMailToType("AGENT");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(userEmails.toArray(new String[0]));
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(incidentEmailVO.getAgentName());
            incidentEmailVO.populateEscalationMatrices();
            // prepare notifications list
            //agent notification
            userNotifications.add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
            //agent manager/lead notification
            for (UserVO userVO : userVOSet) {
                userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
            incidentEmailVO.setNotifications(userNotifications);
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        } else {
            if (dlEmails.size() > 0) {
                incidentEmailVO.setMailToType("AGENT_DL");
                incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                incidentEmailVO.setCc(userEmails.toArray(new String[0]));
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName("DL");
                incidentEmailVO.populateEscalationMatrices();
                for (UserVO userVO : userVOSet) {
                    userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
                }
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
        /*need data for email*/
    }

    @Transactional
    @Async("asyncExecutor")
    public void organisationUpdate(Organisation organisation, Boolean newEntity) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> mailValues = new HashMap<String, Object>();
        mailValues.put("org_name", organisation.getName());
        mailValues.put("valid_from", organisation.getLicenseStart().format(DateTimeFormatter.ofPattern(dateFormat)));
        mailValues.put("valid_to", organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)));
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        mailValues.put("recipientType", "Site Admin");
        mailValues.put("recipients", Arrays.asList(loggedInUser.getEmail()));
        mailValues.put("userToNotify", Collections.singletonList(userVO));
        appEmailService.organisationUpdate(mailValues, newEntity);
        if (!newEntity) {
            // for org admin
            List<UserVO> userVOS = userService.getUsersByRoles(Collections.singletonList("ORG_ADMIN"));
            mailValues.put("recipientType", "Organization Admin");
            mailValues.put("recipients", userVOS.stream().map(UserVO::getEmail).collect(Collectors.toList()));
            mailValues.put("userToNotify", userVOS);
            appEmailService.organisationUpdate(mailValues, newEntity);
        }
    }

    @Transactional
    @Async("asyncExecutor")
    public void organisationNotification(Organisation organisation, Boolean expired) {
        Map<String, Object> mailValues = new HashMap<String, Object>();
        mailValues.put("org_name", organisation.getName());
        mailValues.put("adminTeamEmail", appReplyToEmail);
        mailValues.put("expired_date", organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)));
        mailValues.put("recipientType", organisation.getName().concat(" Team"));
        List<String> orgEmails = organisation.getContactPersons().stream().filter(i -> {
            return i.getEmail() != null;
        }).map(ContactPerson::getEmail).collect(Collectors.toList());
        mailValues.put("recipients", orgEmails);
        mailValues.put("userToNotify", new ArrayList<UserVO>());
        appEmailService.organisationNotification(mailValues, expired);

        mailValues.put("org_name", organisation.getName());
        mailValues.put("adminTeamEmail", appReplyToEmail);
        mailValues.put("expired_date", organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)));
        mailValues.put("recipientType", "Site Admin");
        List<UserVO> users = userService.getAdminUsers();
        orgEmails = users.stream().filter(i -> {
            return i.getEmail() != null;
        }).map(UserVO::getEmail).collect(Collectors.toList());
        mailValues.put("recipients", orgEmails);
        mailValues.put("userToNotify", users);
        appEmailService.organisationNotification(mailValues, expired);
    }

    @Transactional
    @Async("asyncExecutor")
    public void sendOnboardMail(UserVO userVO, Map<String, Object> mailValues) {
        if (userVO.getRoles().contains(BigInteger.valueOf(Long.valueOf("3")))) {
            // if user org admin
            Organisation organisation = organisationService.getOrganisationById(userVO.getOrganisationId());
            mailValues.put("org_name", organisation.getName());
            mailValues.put("valid_from", organisation.getLicenseStart().format(DateTimeFormatter.ofPattern(dateFormat)));
            mailValues.put("valid_to", organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)));
            mailValues.put("recipientType", "Organization Admin");
            mailValues.put("recipients", Arrays.asList(userVO.getEmail()));
            mailValues.put("userToNotify", Collections.singletonList(userVO));
            appEmailService.organisationUpdate(mailValues, true);
        }
        appEmailService.sendOnboardMail(userVO, mailValues);
    }

    @Async("asyncExecutor")
    public void sendBatchReport(Map<String, Object> mailValues) {
        appEmailService.sendBatchReport(mailValues);
    }

    @Async("asyncExecutor")
    public void sendOutBoundAssetUpdateEmail(AssetOrder assetOrder) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ZonedDateTime date = null;
        Map<String, Object> mailValues = new HashMap<String, Object>();
        mailValues.put("currency", assetOrder.getCurrency());
        mailValues.put("ord_no", assetOrder.getOrderNo());
        mailValues.put("dept_name", (assetOrder.getDepartment() != null) ? assetOrder.getDepartment().getName() : "");
        mailValues.put("loc_name", (assetOrder.getLocation() != null) ? assetOrder.getLocation().getName() : "");
        mailValues.put("asset_type", assetOrder.getSubModuleName());
        mailValues.put("product_type", assetOrder.getModuleName());
        mailValues.put("qty", assetOrder.getQuantity());
        mailValues.put("model", assetOrder.getModel());
        mailValues.put("req_name", assetOrder.getRaisedUser().getFirstName() + " " + assetOrder.getRaisedUser().getLastName());
        mailValues.put("req_id", assetOrder.getRaisedUser().getEmployeeId());
        mailValues.put("req_email", assetOrder.getRaisedUser().getEmail());
        mailValues.put("req_cnt_no", assetOrder.getRaisedUser().getContactNo());
        mailValues.put("in_budget", assetOrder.getWithinBudget() ? "Yes" : "No");
        mailValues.put("totalAmount", assetOrder.getTotalAmount());
        mailValues.put("limit", !assetOrder.getWithinBudget() ? assetOrder.getLimitAmount() : "0");
        mailValues.put("extra", !assetOrder.getWithinBudget() ? assetOrder.getExtraAmount() : "0");
        mailValues.put("purchase_type", assetOrder.getPurchaseType().name());
        mailValues.put("existing_agreement", assetOrder.getExistingAgreement() ? "Yes" : "No");
        date = ZonedDateTime.of(assetOrder.getCreatedDate(), ZoneId.of(loggedInUser.getTimeZone()));
        mailValues.put("req_date", date.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(dateFormat)));
        if (assetOrder.getAgreementEndAt() != null) {
            date = ZonedDateTime.of(assetOrder.getAgreementEndAt(), ZoneId.of(loggedInUser.getTimeZone()));
            mailValues.put("agreement_end_date", date.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(dateFormat)));
        } else {
            mailValues.put("agreement_end_date", "");
        }
        /*if (assetOrder.getRentStartAt() != null && assetOrder.getRentEndAt() != null) {
            date = ZonedDateTime.of(assetOrder.getRentStartAt(), ZoneId.of(loggedInUser.getTimeZone()));
            mailValues.put("rent_start_date", date.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(dateFormat)));
            date = ZonedDateTime.of(assetOrder.getRentEndAt(), ZoneId.of(loggedInUser.getTimeZone()));
            mailValues.put("rent_end_date", date.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(dateFormat)));
        } else {
            mailValues.put("rent_start_date", "");
            mailValues.put("rent_end_date", "");
        }*/
        mailValues.put("rent_start_date", "");
        mailValues.put("rent_end_date", assetOrder.getRentDuration());
        mailValues.put("vendor_name", assetOrder.getVendor() == null? "Other" : assetOrder.getVendor().getName());
        mailValues.put("approver_index", 0);
        mailValues.put("order_id", assetOrder.getId());
        mailValues.put("app1_name", assetOrder.getApproverUser1().getFirstName() + " " + assetOrder.getApproverUser1().getLastName());
        mailValues.put("app1_cnt_no", assetOrder.getApproverUser1().getContactNo());
        mailValues.put("approver_1", assetOrder.getApproverUser1().getEmail());
        mailValues.put("app2_name", assetOrder.getApproverUser2().getFirstName() + " " + assetOrder.getApproverUser2().getLastName());
        mailValues.put("app2_cnt_no", assetOrder.getApproverUser2().getContactNo());
        mailValues.put("approver_2", assetOrder.getApproverUser2().getEmail());
        if (assetOrder.getApproverUser1Comment() == null && assetOrder.getApproverUser2Comment() == null) {
            mailValues.put("notification", new Notification(null, null, assetOrder.getApproverUser1(), Status.PUSHED, NotificationType.ACTIONABLE));
            mailValues.put("recipient_name", assetOrder.getApproverUser1().getFirstName() + " " + assetOrder.getApproverUser1().getLastName());
            mailValues.put("feedback", "");
            mailValues.put("ord_status", "");
            mailValues.put("approver_index", 1);
            mailValues.put("subject", "approver1MailSubject");
            mailValues.put("body", "approver1MailBody");
            mailValues.put("to", assetOrder.getApproverUser1().getEmail());
            appEmailService.sendOutBoundAssetUpdateEmail(mailValues);
            mailValues.put("notification", new Notification(null, null, assetOrder.getRaisedUser(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetOrder.getRaisedUser().getFirstName() + " " + assetOrder.getRaisedUser().getLastName());
            mailValues.put("feedback", "");
            mailValues.put("ord_status", "");
            mailValues.put("subject", "mailSubject");
            mailValues.put("body", "mailBody");
            mailValues.put("to", assetOrder.getRaisedUser().getEmail());
            appEmailService.sendOutBoundAssetUpdateEmail(mailValues);
        } else if (assetOrder.getApproverUser1Comment() != null && assetOrder.getApproverUser2Comment() == null) {
            if (assetOrder.getApprovedUser1()) {
                mailValues.put("notification", new Notification(null, null, assetOrder.getApproverUser2(), Status.PUSHED, NotificationType.ACTIONABLE));
                mailValues.put("recipient_name", assetOrder.getApproverUser2().getFirstName() + " " + assetOrder.getApproverUser2().getLastName());
                mailValues.put("feedback", "");
                mailValues.put("ord_status", "");
                mailValues.put("approver_index", 2);
                mailValues.put("subject", "approver2MailSubject");
                mailValues.put("body", "approver2MailBody");
                mailValues.put("to", assetOrder.getApproverUser2().getEmail());
                appEmailService.sendOutBoundAssetUpdateEmail(mailValues);
            }
            mailValues.put("notification", new Notification(null, null, assetOrder.getRaisedUser(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetOrder.getRaisedUser().getFirstName() + " " + assetOrder.getRaisedUser().getLastName());
            mailValues.put("feedback", assetOrder.getApproverUser1Comment());
            mailValues.put("ord_status", assetOrder.getApprovedUser1() ? "Approved" : "Rejected");
            mailValues.put("subject", "approver1FeedbackMailSubject");
            mailValues.put("body", "approver1FeedbackMailBody");
            mailValues.put("to", assetOrder.getRaisedUser().getEmail());
            appEmailService.sendOutBoundAssetUpdateEmail(mailValues);
        } else if (assetOrder.getApproverUser1Comment() != null && assetOrder.getApproverUser2Comment() != null) {
            mailValues.put("notification", new Notification(null, null, assetOrder.getApproverUser1(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetOrder.getApproverUser1().getFirstName() + " " + assetOrder.getApproverUser1().getLastName());
            mailValues.put("feedback", assetOrder.getApproverUser2Comment());
            mailValues.put("ord_status", assetOrder.getApprovedUser2() ? "Approved" : "Rejected");
            mailValues.put("subject", "approver2FeedbackMailSubject");
            mailValues.put("body", "approver2FeedbackMailBody");
            mailValues.put("to", assetOrder.getApproverUser1().getEmail());
            appEmailService.sendOutBoundAssetUpdateEmail(mailValues);
            mailValues.put("notification", new Notification(null, null, assetOrder.getRaisedUser(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetOrder.getRaisedUser().getFirstName() + " " + assetOrder.getRaisedUser().getLastName());
            mailValues.put("feedback", assetOrder.getApproverUser2Comment());
            mailValues.put("ord_status", assetOrder.getApprovedUser2() ? "Approved" : "Rejected");
            mailValues.put("subject", "approver2FeedbackMailSubject");
            mailValues.put("body", "approver2FeedbackMailBody");
            mailValues.put("to", assetOrder.getRaisedUser().getEmail());
            appEmailService.sendOutBoundAssetUpdateEmail(mailValues);
        }
    }

    @Transactional
    @Async("asyncExecutor")
    public void sendInboundAssetRequestActionEmail(IncidentEmailVO incidentEmailVO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<DistributionList> distributionLists = new ArrayList<DistributionList>();
        List<String> dlEmails = new ArrayList<String>();
        if (incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            distributionLists = distributionListService.getByModuleIdAndSubModuleId(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId());
            dlEmails = distributionLists.stream().map(DistributionList::getDlEmail).collect(Collectors.toList());
        }
        List<UserVO> userVOS = userService.getUsersByRoles(Arrays.asList("ORG_ASSET_AGENT_LEAD", "ORG_ASSET_AGENT_MANAGER"));
        if (incidentEmailVO.getFeedbackProvided() && incidentEmailVO.getAssetApproved()) {
            if (dlEmails.size() > 0) {
                incidentEmailVO.setMailSubjectKey("astDlSub");
                incidentEmailVO.setMailBodyKey("astDlCnt");
                incidentEmailVO.setMailToType("AGENT_DL");
                incidentEmailVO.setTo(dlEmails.toArray(new String[0]));
                incidentEmailVO.setCc(new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.setRecipientName("DL");
                incidentEmailVO.populateEscalationMatrices();
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>());
        List<UserVO> users = userService.getUsersByEmails(userEmails);
        for (UserVO userVO : users) {
            incidentEmailVO.setMailSubjectKey("astMngrFdbckSub");
            incidentEmailVO.setMailBodyKey("astMngrFdbckCnt");
            incidentEmailVO.setMailToType("EMP");
            incidentEmailVO.setTo(new String[]{userVO.getEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.setRecipientName(userVO.getFullName());
            incidentEmailVO.populateEscalationMatrices();
            incidentEmailVO.setNotifications(Collections.singletonList(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO)));
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        }
    }
}