package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.utility.Utility;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.AppConfigRepository;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MiscService {

    private static final Logger log = LoggerFactory.getLogger(MiscService.class);

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

    @Autowired
    private NotificationService notificationService;

    /**
     * Onboard request mail
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
        for (Map<String, String> data : dataList) {
            user = new User();
            if (data.get("FIRST_NAME") == null || data.get("FIRST_NAME").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                user.setFirstName(data.get("FIRST_NAME").trim());
            }
            if (data.get("LAST_NAME") == null || data.get("LAST_NAME").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                user.setLastName(data.get("LAST_NAME").trim());
            }
            if (data.get("EMAIL") == null || data.get("EMAIL").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                User u = userService.getUserByEmail(data.get("EMAIL").trim());
                if (u != null) {
                    continue;
                }
                user.setEmail(data.get("EMAIL").trim());
            }
            if (data.get("CONTACT_NO") == null || data.get("CONTACT_NO").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                user.setContactNo(data.get("CONTACT_NO").trim());
            }
            if (data.get("ROLES") == null || data.get("ROLES").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                List<Role> roles = roleService.getByNames(Arrays.asList(data.get("ROLES").trim().toUpperCase().split(",")));
                if (roles != null && roles.size() > 0) {
                    user.setRoles(roles
                            .stream()
                            .map(Role::getId)
                            .collect(Collectors.toList())
                    );
                } else {
                    continue;
                }
            }
            if (data.get("SEC_CONTACT_NO") == null || data.get("SEC_CONTACT_NO").trim().equals("")) {
                user.setSecContactNo(null);
            } else {
                user.setSecContactNo(data.get("SEC_CONTACT_NO").trim());
            }
            if (data.get("EMP_ID") == null || data.get("EMP_ID").trim().equals("")) {
                user.setEmployeeId(null);
            } else {
                User u = userService.getUserByEmployeeId(data.get("EMP_ID").trim());
                if (u != null) {
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
                user.setManagerId(null);
            } else {
                User u = userService.getUserByEmployeeId(data.get("MANAGER_ID").trim());
                if (u != null) {
                    user.setManagerId(u.getId());
                }
            }
            if (data.get("LOCATION") != null && !data.get("LOCATION").trim().equals("")) {
                Location location = locationService.getByLocationName(data.get("LOCATION").trim());
                if (location != null) {
                    user.setLocation(location);
                }
            }
            if (data.get("DEPARTMENT") != null && !data.get("DEPARTMENT").trim().equals("")) {
                Department department = departmentService.getByDepartmentName(data.get("DEPARTMENT").trim());
                if (department != null) {
                    user.setDepartment(department);
                }
            }
            if (data.get("VENDOR") != null && !data.get("VENDOR").trim().equals("")) {
                Vendor vendor = vendorService.getByName(data.get("VENDOR").trim());
                if (vendor != null) {
                    user.setVendor(vendor);
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
            //System.out.println(data);
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
                    this.sendOnboardMail(userVO, mailValues);
                }
            }
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
            dlEmails = distributionLists.stream()
                    .map(DistributionList::getDlEmail)
                    .collect(Collectors.toList());
        }
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER")
        );
        List<Notification> userNotifications = new ArrayList<Notification>();
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        if (incidentEmailVO.getUserEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
                incidentEmailVO.setMailToType("AGENT");
                incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
                incidentEmailVO.setCc(new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.populateEscalationMatrices();
                log.info("to {} ", incidentEmailVO.getTo());
                log.info("cc {} ", incidentEmailVO.getCc());
                log.info("bcc {} ", incidentEmailVO.getBcc());
                // prepare notifications list
                userNotifications.add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
                incidentEmailVO.setNotifications(userNotifications);
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
                    incidentEmailVO.populateEscalationMatrices();
                    log.info("to {} ", incidentEmailVO.getTo());
                    log.info("cc {} ", incidentEmailVO.getCc());
                    log.info("bcc {} ", incidentEmailVO.getBcc());
                    appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
                }
            }
            if (incidentEmailVO.getNewIncident()) {
                incidentEmailVO.setMailToType("EMP");
                incidentEmailVO.setTo(new String[]{incidentEmailVO.getUserEmail()});
                incidentEmailVO.setCc(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? incidentEmailVO.getWatchList().split(",").clone() : new String[]{});
                incidentEmailVO.setBcc(new String[]{});
                incidentEmailVO.setReplyTo(appReplyToEmail);
                incidentEmailVO.setCategory(category);
                incidentEmailVO.setSubCategory(subCategory);
                incidentEmailVO.setUserVOS(userVOS);
                incidentEmailVO.populateEscalationMatrices();
                log.info("to {} ", incidentEmailVO.getTo());
                log.info("cc {} ", incidentEmailVO.getCc());
                log.info("bcc {} ", incidentEmailVO.getBcc());
                // prepare notifications list
                List<String> userEmails = new ArrayList<String>() {{
                    add(incidentEmailVO.getUserEmail());
                }};
                incidentEmailVO.setCc(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? incidentEmailVO.getWatchList().split(",").clone() : new String[]{});
                List<UserVO> users = userService.getUsersByEmails(userEmails);
                for (UserVO userVO : users) {
                    userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
                }
                incidentEmailVO.setNotifications(userNotifications);
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        } else {
            incidentEmailVO.setMailToType("EMP");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getUserEmail()});
            incidentEmailVO.setCc(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? incidentEmailVO.getWatchList().split(",").clone() : new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.populateEscalationMatrices();
            log.info("to {} ", incidentEmailVO.getTo());
            log.info("cc {} ", incidentEmailVO.getCc());
            log.info("bcc {} ", incidentEmailVO.getBcc());
            // prepare notifications list
            List<String> userEmails = new ArrayList<String>() {{
                add(incidentEmailVO.getUserEmail());
            }};
            userEmails.addAll(
                    !incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>()
            );
            List<UserVO> users = userService.getUsersByEmails(userEmails);
            for (UserVO userVO : users) {
                userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
            incidentEmailVO.setNotifications(userNotifications);
            appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
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
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER")
        );
        incidentEmailVO.setMailToType("AGENT");
        //prepare agent email
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        //incidentEmailVO.setBcc(bccList.toArray(new String[0]));
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("agent to {} ", incidentEmailVO.getTo());
        log.info("agent cc {} ", incidentEmailVO.getCc());
        log.info("agent bcc {} ", incidentEmailVO.getBcc());
        //agent notifications
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
        }};
        //save notifications
        incidentEmailVO.setNotifications(userNotifications);
        appEmailService.sendIncidentAssignEmail(incidentEmailVO);
        //prepare emp email
        incidentEmailVO.setMailToType("EMP");
        incidentEmailVO.setMailSubjectKey("incidentAssignMailSubject");
        incidentEmailVO.setMailBodyKey("oldMailBody");
        incidentEmailVO.setDescription("Incident assigned to an agent. Please check below details.");
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getUserEmail()});
        incidentEmailVO.setCc(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? incidentEmailVO.getWatchList().split(",").clone() : new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("emp to {} ", incidentEmailVO.getTo());
        log.info("emp cc {} ", incidentEmailVO.getCc());
        log.info("emp bcc {} ", incidentEmailVO.getBcc());
        // prepare notifications list
        userNotifications = new ArrayList<Notification>();
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(
                !incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>()
        );
        List<UserVO> users = userService.getUsersByEmails(userEmails);
        for (UserVO userVO : users) {
            userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
        }
        incidentEmailVO.setNotifications(userNotifications);
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
    }

    /**
     * notify user incident assign via mail and notification
     *
     * @param incidentEmailVO
     */
    @Transactional
    @Async("asyncExecutor")
    public void notifyIncidentAssignViaBatch(IncidentEmailVO incidentEmailVO) {
        //LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //prepare user email
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getSubModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"),
                incidentEmailVO.getOrganisationId()
        );
        incidentEmailVO.setMailToType("AGENT");
        //prepare agent email
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        //incidentEmailVO.setBcc(bccList.toArray(new String[0]));
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("agent to {} ", incidentEmailVO.getTo());
        log.info("agent cc {} ", incidentEmailVO.getCc());
        log.info("agent bcc {} ", incidentEmailVO.getBcc());
        //agent notifications
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
        }};
        //save notifications
        incidentEmailVO.setNotifications(userNotifications);
        appEmailService.sendIncidentAssignEmail(incidentEmailVO);
        //prepare emp email
        incidentEmailVO.setMailToType("EMP");
        incidentEmailVO.setMailSubjectKey("incidentAssignMailSubject");
        incidentEmailVO.setMailBodyKey("oldMailBody");
        incidentEmailVO.setDescription("Incident assigned to an agent. Please check below details.");
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getUserEmail()});
        incidentEmailVO.setCc(!incidentEmailVO.getWatchList().equalsIgnoreCase("") ? incidentEmailVO.getWatchList().split(",").clone() : new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("emp to {} ", incidentEmailVO.getTo());
        log.info("emp cc {} ", incidentEmailVO.getCc());
        log.info("emp bcc {} ", incidentEmailVO.getBcc());
        // prepare notifications list
        userNotifications = new ArrayList<Notification>();
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(
                !incidentEmailVO.getWatchList().equalsIgnoreCase("") ? Arrays.asList(incidentEmailVO.getWatchList().split(",")) : new ArrayList<String>()
        );
        List<UserVO> users = userService.getUsersByEmailsAndOrganisation(userEmails, incidentEmailVO.getOrganisationId());
        for (UserVO userVO : users) {
            userNotifications.add(new Notification(null, null, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
        }
        incidentEmailVO.setNotifications(userNotifications);
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
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
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(
                    incidentEmailVO.getModuleId(),
                    incidentEmailVO.getSubModuleId(),
                    incidentEmailVO.getOrganisationId()
            );
            dlEmails = distributionLists.stream()
                    .map(DistributionList::getDlEmail)
                    .collect(Collectors.toList());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"),
                incidentEmailVO.getOrganisationId()
        );
        List<Notification> userNotifications = new ArrayList<Notification>();
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        incidentEmailVO.setMailSubjectKey("incident50MailSubject");
        incidentEmailVO.setMailBodyKey("oldMailBody");
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
            incidentEmailVO.setMailToType("AGENT");
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            incidentEmailVO.setCc(new String[]{});
            incidentEmailVO.setBcc(new String[]{});
            incidentEmailVO.setReplyTo(appReplyToEmail);
            incidentEmailVO.setCategory(category);
            incidentEmailVO.setSubCategory(subCategory);
            incidentEmailVO.setUserVOS(userVOS);
            incidentEmailVO.populateEscalationMatrices();
            log.info("to {} ", incidentEmailVO.getTo());
            log.info("cc {} ", incidentEmailVO.getCc());
            log.info("bcc {} ", incidentEmailVO.getBcc());
            // prepare notifications list
            userNotifications.add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
            incidentEmailVO.setNotifications(userNotifications);
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
                incidentEmailVO.populateEscalationMatrices();
                log.info("to {} ", incidentEmailVO.getTo());
                log.info("cc {} ", incidentEmailVO.getCc());
                log.info("bcc {} ", incidentEmailVO.getBcc());
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
        /*need data for email*/
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
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(
                    incidentEmailVO.getModuleId(),
                    incidentEmailVO.getSubModuleId(),
                    incidentEmailVO.getOrganisationId()
            );
            dlEmails = distributionLists.stream()
                    .map(DistributionList::getDlEmail)
                    .collect(Collectors.toList());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"),
                incidentEmailVO.getOrganisationId()
        );
        incidentEmailVO.setMailSubjectKey("incident75MailSubject");
        incidentEmailVO.setMailBodyKey("oldMailBody");
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
            incidentEmailVO.populateEscalationMatrices();
            log.info("to {} ", incidentEmailVO.getTo());
            log.info("cc {} ", incidentEmailVO.getCc());
            log.info("bcc {} ", incidentEmailVO.getBcc());
            // prepare notifications list
            userNotifications.add(new Notification(null, null, new User(incidentEmailVO.getAgentVersion(), incidentEmailVO.getAgentId()), Status.PUSHED, NotificationType.INFO));
            incidentEmailVO.setNotifications(userNotifications);
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
                incidentEmailVO.populateEscalationMatrices();
                log.info("to {} ", incidentEmailVO.getTo());
                log.info("cc {} ", incidentEmailVO.getCc());
                log.info("bcc {} ", incidentEmailVO.getBcc());
                appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
            }
        }
        /*need data for email*/
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
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(
                    incidentEmailVO.getModuleId(),
                    incidentEmailVO.getSubModuleId(),
                    incidentEmailVO.getOrganisationId()
            );
            dlEmails = distributionLists.stream()
                    .map(DistributionList::getDlEmail)
                    .collect(Collectors.toList());
        }
        UserVO agentManagerUserVO = null;
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA") && incidentEmailVO.getAgentManagerId() != null) {
            agentManagerUserVO = userService.getUserById(incidentEmailVO.getAgentManagerId());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"),
                incidentEmailVO.getOrganisationId()
        );
        if (agentManagerUserVO != null) {
            userVOS.add(agentManagerUserVO);
        }
        Set<String> userEmails = new HashSet<String>();
        Set<UserVO> userVOSet = new HashSet<UserVO>();
        for (UserVO userVO : userVOS) {
            List<String> roles = userVO.getRoleNames();
            for (String s : roles) {
                if (userVO.getEmail().equals(incidentEmailVO.getAgentEmail())
                        || s.equals("ORG_INCIDENT_AGENT_LEAD")
                        || (incidentEmailVO.getAgentManagerId() != null
                        && userVO.getId().equals(incidentEmailVO.getAgentManagerId()))
                ) {
                    userEmails.add(userVO.getEmail());
                    userVOSet.add(userVO);
                }
            }
        }
        incidentEmailVO.setMailSubjectKey("incidentSlaBreachedMailSubject");
        incidentEmailVO.setMailBodyKey("oldMailBody");
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
            incidentEmailVO.populateEscalationMatrices();
            log.info("to {} ", incidentEmailVO.getTo());
            log.info("cc {} ", incidentEmailVO.getCc());
            log.info("bcc {} ", incidentEmailVO.getBcc());
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
                incidentEmailVO.populateEscalationMatrices();
                log.info("to {} ", incidentEmailVO.getTo());
                log.info("cc {} ", incidentEmailVO.getCc());
                log.info("bcc {} ", incidentEmailVO.getBcc());
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
            distributionLists = distributionListService.getByModuleIdAndSubModuleIdAndOrganisation(
                    incidentEmailVO.getModuleId(),
                    incidentEmailVO.getSubModuleId(),
                    incidentEmailVO.getOrganisationId()
            );
            dlEmails = distributionLists.stream()
                    .map(DistributionList::getDlEmail)
                    .collect(Collectors.toList());
        }
        UserVO agentManagerUserVO = null;
        if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA") && incidentEmailVO.getAgentManagerId() != null) {
            agentManagerUserVO = userService.getUserById(incidentEmailVO.getAgentManagerId());
        }
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"),
                incidentEmailVO.getOrganisationId()
        );
        if (agentManagerUserVO != null) {
            userVOS.add(agentManagerUserVO);
        }
        Set<String> userEmails = new HashSet<String>();
        Set<UserVO> userVOSet = new HashSet<UserVO>();
        for (UserVO userVO : userVOS) {
            List<String> roles = userVO.getRoleNames();
            for (String s : roles) {
                if (userVO.getEmail().equals(incidentEmailVO.getAgentEmail())
                        || s.equals("ORG_INCIDENT_AGENT_MANAGER")
                        || s.equals("ORG_INCIDENT_AGENT_LEAD")
                        || (incidentEmailVO.getAgentManagerId() != null
                        && userVO.getId().equals(incidentEmailVO.getAgentManagerId()))
                ) {
                    userEmails.add(userVO.getEmail());
                    userVOSet.add(userVO);
                }
            }
        }
        incidentEmailVO.setMailSubjectKey("incidentSlaBreached60MinPassedMailSubject");
        incidentEmailVO.setMailBodyKey("oldMailBody");
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
            incidentEmailVO.populateEscalationMatrices();
            log.info("to {} ", incidentEmailVO.getTo());
            log.info("cc {} ", incidentEmailVO.getCc());
            log.info("bcc {} ", incidentEmailVO.getBcc());
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
                incidentEmailVO.populateEscalationMatrices();
                log.info("to {} ", incidentEmailVO.getTo());
                log.info("cc {} ", incidentEmailVO.getCc());
                log.info("bcc {} ", incidentEmailVO.getBcc());
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
}