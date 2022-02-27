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
        //String categoryAdminRole = "ORG_" + category + "_CATEGORY_ADMIN";
        //userService.getUsersByRoles(Collections.singletonList(categoryAdminRole));
        if (incidentEmailVO.getUserEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
                incidentEmailVO.setMailSubjectKey(getMailSubjectKeyByStatus(
                        incidentEmailVO.getStatus(),
                        incidentEmailVO.getReopened()
                        )
                );
                incidentEmailVO.setMailBodyKey("oldMailBody");
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
                    incidentEmailVO.setMailSubjectKey("newDlMailSubject");
                    incidentEmailVO.setMailBodyKey("newDlMailBody");
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
                    incidentEmailVO.setMailSubjectKey("newEmpMailSubject");
                    incidentEmailVO.setMailBodyKey("newEmpMailBody");
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
                incidentEmailVO.setMailSubjectKey(getMailSubjectKeyByStatus(
                        incidentEmailVO.getStatus(),
                        incidentEmailVO.getReopened()
                        )
                );
                incidentEmailVO.setMailBodyKey("newEmpMailBody");
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

    private String getMailSubjectKeyByStatus(String status, Boolean reopened) {
        if (status.equalsIgnoreCase("WORK_IN_PROGRESS")) {
            return "wipSubject";
        } else if (status.equalsIgnoreCase("NEED_CLARIFICATION")) {
            return "needClarificationSubject";
        } else if (status.equalsIgnoreCase("CLARIFICATION_PROVIDED")) {
            return "clarificationProvidedSubject";
        } else if (status.equalsIgnoreCase("ON_HOLD")) {
            return "onHoldSubject";
        } else if (status.equalsIgnoreCase("PENDING_FROM_VENDOR")) {
            return "pending3rdPartySubject";
        } else if (status.equalsIgnoreCase("CLOSED")) {
            return "closeMailSubject";
        } else if (reopened) {
            return "reopenMailSubject";
        }
        return "oldMailSubject";
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
        incidentEmailVO.setMailSubjectKey("mailAssignSubject");
        incidentEmailVO.setMailBodyKey("mailAssignBody");
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
            incidentEmailVO.setMailSubjectKey("incidentAssignMailSubject");
            incidentEmailVO.setMailBodyKey("oldMailBody");
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
        List<UserVO> userVOS = userService.getUsersByRolesAndOrganisation(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER"),
                incidentEmailVO.getOrganisationId()
        );
        //prepare agent email
        incidentEmailVO.setMailToType("AGENT");
        incidentEmailVO.setMailSubjectKey("mailAssignSubject");
        incidentEmailVO.setMailBodyKey("mailAssignBody");
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
            incidentEmailVO.setMailSubjectKey("incidentAssignMailSubject");
            incidentEmailVO.setMailBodyKey("oldMailBody");
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
        List<String> orgEmails = organisation.getContactPersons().stream()
                .filter(i -> {
                    return i.getEmail() != null;
                })
                .map(ContactPerson::getEmail)
                .collect(Collectors.toList());
        mailValues.put("recipients", orgEmails);
        mailValues.put("userToNotify", new ArrayList<UserVO>());
        appEmailService.organisationNotification(mailValues, expired);

        mailValues.put("org_name", organisation.getName());
        mailValues.put("adminTeamEmail", appReplyToEmail);
        mailValues.put("expired_date", organisation.getLicenseEnd().format(DateTimeFormatter.ofPattern(dateFormat)));
        mailValues.put("recipientType", "Site Admin");
        List<UserVO> users = userService.getAdminUsers();
        orgEmails = users.stream()
                .filter(i -> {
                    return i.getEmail() != null;
                })
                .map(UserVO::getEmail)
                .collect(Collectors.toList());
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
        Map<String, Object> mailValues = new HashMap<String, Object>();
        mailValues.put("ord_no", assetOrder.getOrderNo());
        mailValues.put("dept_name", (assetOrder.getDepartment() != null) ? assetOrder.getDepartment().getName() : "");
        mailValues.put("loc_name", (assetOrder.getLocation() != null) ? assetOrder.getLocation().getOfficeName() : "");
        mailValues.put("asset_type", assetOrder.getAssetType().name());
        mailValues.put("qty", assetOrder.getQuantity());
        mailValues.put("cost", assetOrder.getCost());
        mailValues.put("in_budget", assetOrder.getWithinBudget() ? "Yes" : "No");
        mailValues.put("purchase_type", assetOrder.getPurchaseType().name());
        mailValues.put("existing_agreement", assetOrder.getExistingAgreement() ? "Yes" : "No");
        mailValues.put("vendor_name", assetOrder.getVendor().getName());
        mailValues.put("comment", assetOrder.getComment());
        mailValues.put("approver_index", 0);
        mailValues.put("order_id", assetOrder.getId());
        mailValues.put("approver_1", assetOrder.getApproverUser1().getEmail());
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

    @Async("asyncExecutor")
    public void sendInboundAssetRequestUpdateEmail(AssetRequest assetRequest, String oldSerialNo) {
        Map<String, Object> mailValues = new HashMap<String, Object>();
        mailValues.put("productCategory", assetRequest.getProductCategory().name());
        mailValues.put("assetType", assetRequest.getAssetType().name());
        mailValues.put("modelNo", assetRequest.getModelNo() != null ? assetRequest.getModelNo() : "");
        mailValues.put("serialNo", oldSerialNo == null ? assetRequest.getAsset() != null ? assetRequest.getAsset().getSerialNo() : "" : oldSerialNo);
        mailValues.put("user", assetRequest.getUser().getFirstName() + " " + assetRequest.getUser().getLastName());
        mailValues.put("project", assetRequest.getUser().getProjectCode());
        mailValues.put("longTerm", assetRequest.getLongTerm() ? "YES" : "NO");
        mailValues.put("comment", assetRequest.getComment());
        mailValues.put("asset_id", assetRequest.getId());
        mailValues.put("ord_no", assetRequest.getAssetRequestNo());
        if (assetRequest.getItTeamComment() != null) {
            mailValues.put("notification", new Notification(null, null, assetRequest.getUser(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetRequest.getUser().getFirstName() + " " + assetRequest.getUser().getLastName());
            mailValues.put("feedback", assetRequest.getItTeamComment());
            mailValues.put("ord_status", "");
            mailValues.put("subject", assetRequest.getAllocated() ? "allocatedMailSubject" : "deallocatedMailSubject");
            mailValues.put("body", assetRequest.getAllocated() ? "allocatedMailBody" : "deallocatedMailBody");
            mailValues.put("to", assetRequest.getUser().getEmail());
            appEmailService.sendInboundAssetRequestUpdateEmail(mailValues);
            User manager = new User(userService.getUserById(assetRequest.getUser().getManagerId()));
            mailValues.put("notification", new Notification(null, null, manager, Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", manager.getFirstName() + " " + manager.getLastName());
            mailValues.put("feedback", assetRequest.getItTeamComment());
            mailValues.put("ord_status", "");
            mailValues.put("subject", assetRequest.getAllocated() ? "allocatedMailSubject" : "deallocatedMailSubject");
            mailValues.put("body", assetRequest.getAllocated() ? "allocatedMailBody" : "deallocatedMailBody");
            mailValues.put("to", assetRequest.getUser().getEmail());
            appEmailService.sendInboundAssetRequestUpdateEmail(mailValues);
        } else if (!assetRequest.getAllocated() && assetRequest.getApproverComment() == null) {
            User manager = new User(userService.getUserById(assetRequest.getUser().getManagerId()));
            mailValues.put("notification", new Notification(null, null, manager, Status.PUSHED, NotificationType.ACTIONABLE));
            mailValues.put("recipient_name", manager.getFirstName() + " " + manager.getLastName());
            mailValues.put("feedback", "");
            mailValues.put("ord_status", "");
            mailValues.put("subject", "managerMailSubject");
            mailValues.put("body", "managerMailBody");
            mailValues.put("to", manager.getEmail());
            appEmailService.sendInboundAssetRequestUpdateEmail(mailValues);
            mailValues.put("notification", new Notification(null, null, assetRequest.getUser(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetRequest.getUser().getFirstName() + " " + assetRequest.getUser().getLastName());
            mailValues.put("feedback", "");
            mailValues.put("ord_status", "");
            mailValues.put("subject", "mailSubject");
            mailValues.put("body", "mailBody");
            mailValues.put("to", assetRequest.getUser().getEmail());
            appEmailService.sendInboundAssetRequestUpdateEmail(mailValues);
        } else if (!assetRequest.getAllocated() && assetRequest.getApproverComment() != null) {
            if (assetRequest.getApproved()) {
                List<UserVO> userVOS = userService.getUsersByRoles(Collections.singletonList("ORG_MANAGE_ASSET"));
                for (UserVO userVO : userVOS) {
                    mailValues.put("notification", new Notification(null, null, new User(userVO), Status.PUSHED, NotificationType.ACTIONABLE));
                    mailValues.put("recipient_name", userVO.getFirstName() + " " + userVO.getLastName());
                    mailValues.put("feedback", assetRequest.getApproverComment());
                    mailValues.put("ord_status", assetRequest.getApproved() ? "Approved" : "Rejected");
                    mailValues.put("subject", "managerFeedbackMailSubject");
                    mailValues.put("body", "managerFeedbackMailBody");
                    mailValues.put("to", userVO.getEmail());
                    appEmailService.sendInboundAssetRequestUpdateEmail(mailValues);
                }
            }
            mailValues.put("notification", new Notification(null, null, assetRequest.getUser(), Status.PUSHED, NotificationType.INFO));
            mailValues.put("recipient_name", assetRequest.getUser().getFirstName() + " " + assetRequest.getUser().getLastName());
            mailValues.put("feedback", assetRequest.getApproverComment());
            mailValues.put("ord_status", assetRequest.getApproved() ? "Approved" : "Rejected");
            mailValues.put("subject", "managerFeedbackMailSubject");
            mailValues.put("body", "managerFeedbackMailBody");
            mailValues.put("to", assetRequest.getUser().getEmail());
            appEmailService.sendInboundAssetRequestUpdateEmail(mailValues);
        }
    }
}