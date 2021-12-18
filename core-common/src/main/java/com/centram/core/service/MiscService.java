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
    private DepartmentService departmentService;

    @Autowired
    private RoleService roleService;

    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Value("${app.date.time.format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
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
                List<Role> roles = roleService.getByRoleNames(Arrays.asList(data.get("ROLES").trim().toUpperCase().split(",")));
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
            user.setOrganisation(organisationService.getOrganisationById(loggedInUserDTO.getOrganisationId()));
            password = Utility.getUniqueString(8);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(Status.ACTIVE);
            user.setId(null);
            users.add(user);

            userVO = new UserVO(user);
            userVO.setPassword(password);
            userVOS.add(userVO);

            //System.out.println(data);
        }
        log.info("{}", users);
        if (users.size() > 0) {
            userService.saveUsers(users);
            Map<String, String> mailValues = new HashMap<>();
            for (UserVO uv : userVOS) {
                mailValues = new HashMap<>();
                log.info("email : {}, password: {}", userVO.getEmail(), userVO.getPassword());
                mailValues.put("password", userVO.getPassword());
                appEmailService.sendOnboardMail(userVO, mailValues);
            }
        }
    }

    /**
     * notify user incident update via mail and notification
     *
     * @param incidentEmailVO
     */
    @Async("asyncExecutor")
    public void notifyIncidentUpdate(IncidentEmailVO incidentEmailVO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER", category, subCategory)
        );
        List<String> bccList = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.contains("USER") || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                })
                .map(UserVO::getEmail)
                .collect(Collectors.toList());
        if (incidentEmailVO.getUserEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            if (!incidentEmailVO.getAgentEmail().equalsIgnoreCase("NA")) {
                incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
            } else {
                incidentEmailVO.setTo(bccList.toArray(new String[0]));
            }
            incidentEmailVO.setCc(new String[]{loggedInUser.getEmail()});
        } else {
            incidentEmailVO.setTo(new String[]{incidentEmailVO.getUserEmail()});
            incidentEmailVO.setCc(new String[]{incidentEmailVO.getAgentEmail()});
        }
        incidentEmailVO.setBcc(incidentEmailVO.getWatchList().split(",").clone());
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("to {} ", incidentEmailVO.getTo());
        log.info("cc {} ", incidentEmailVO.getCc());
        log.info("bcc {} ", incidentEmailVO.getBcc());
        //agent notifications
        List<UserVO> agentUserVOS = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.contains("AGENT") || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            String title = "[".concat(incidentEmailVO.getIncidentNo()).concat("]");
            String body = incidentEmailVO.getDescription();
            for (UserVO userVO : agentUserVOS) {
                add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
        }};
        //user notification
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(Arrays.asList(incidentEmailVO.getWatchList().split(",")));
        List<UserVO> users = userService.getUsersByEmails(userEmails);
        String title = "[".concat(incidentEmailVO.getIncidentNo()).concat("]");
        String body = "Agent will contact you soon...";
        for (UserVO userVO : users) {
            userNotifications.add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
        }
        //save notifications
        notificationService.save(userNotifications);
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        /*need data for email*/
    }

    /**
     * notify user incident assign via mail and notification
     *
     * @param incidentEmailVO
     */
    @Async("asyncExecutor")
    public void notifyIncidentAssign(IncidentEmailVO incidentEmailVO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //prepare user email
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER", category, subCategory)
        );
        List<String> bccList = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.contains("USER") || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                })
                .map(UserVO::getEmail)
                .collect(Collectors.toList());
        bccList.addAll(Arrays.asList(incidentEmailVO.getWatchList().split(",")));
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getUserEmail()});
        incidentEmailVO.setCc(incidentEmailVO.getWatchList().split(","));
        incidentEmailVO.setBcc(new String[]{});
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("user to {} ", incidentEmailVO.getTo());
        log.info("user cc {} ", incidentEmailVO.getCc());
        log.info("user bcc {} ", incidentEmailVO.getBcc());
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        //agent notifications
        List<UserVO> agentUserVOS = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (incidentEmailVO.getAgentEmail().equals(i.getEmail()) || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            String title = "[".concat(incidentEmailVO.getIncidentNo()).concat("]");
            String body = "[".concat(incidentEmailVO.getIncidentNo()).concat("]").concat(" assigned to ").concat(incidentEmailVO.getAgentName());
            for (UserVO userVO : agentUserVOS) {
                add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
        }};
        //user notification
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(Arrays.asList(incidentEmailVO.getWatchList().split(",")));
        List<UserVO> users = userService.getUsersByEmails(userEmails);
        String title = "[".concat(incidentEmailVO.getIncidentNo()).concat("]");
        String body = incidentEmailVO.getAgentName().concat(" will work on this incident.");
        for (UserVO userVO : users) {
            userNotifications.add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
        }
        //save notifications
        notificationService.save(userNotifications);
        //prepare agent email
        bccList = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                })
                .map(UserVO::getEmail)
                .collect(Collectors.toList());
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(bccList.toArray(new String[0]));
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        log.info("agent to {} ", incidentEmailVO.getTo());
        log.info("agent cc {} ", incidentEmailVO.getCc());
        log.info("agent bcc {} ", incidentEmailVO.getBcc());
        appEmailService.sendIncidentAssignEmail(incidentEmailVO);
    }

    @Async("asyncExecutor")
    public void notifyWip50PercentTimePassed(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER", category, subCategory)
        );
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        //agent notifications
        List<UserVO> agentUserVOS = userVOS.stream()
                .filter(i -> {
                    return i.getEmail().equalsIgnoreCase(incidentEmailVO.getAgentEmail());
                })
                .collect(Collectors.toList());
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            String title = "Incident [".concat(incidentEmailVO.getIncidentNo()).concat("]");
            String body = incidentEmailVO.getIncidentNo().substring(0, Math.min(incidentEmailVO.getIncidentNo().length(), 50)).concat("...");
            for (UserVO userVO : agentUserVOS) {
                add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
        }};
        //save notifications
        notificationService.save(userNotifications);
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        /*need data for email*/
    }

    @Async("asyncExecutor")
    public void notifyWip75PercentTimePassed(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER", category, subCategory)
        );
        incidentEmailVO.setTo(new String[]{incidentEmailVO.getAgentEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(new String[]{});
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        //agent notifications
        List<UserVO> agentUserVOS = userVOS.stream()
                .filter(i -> {
                    return i.getEmail().equalsIgnoreCase(incidentEmailVO.getAgentEmail());
                })
                .collect(Collectors.toList());
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            String title = "Incident [".concat(incidentEmailVO.getIncidentNo()).concat("]");
            String body = incidentEmailVO.getIncidentNo().substring(0, Math.min(incidentEmailVO.getIncidentNo().length(), 50)).concat("...");
            for (UserVO userVO : agentUserVOS) {
                add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
        }};
        //save notifications
        notificationService.save(userNotifications);
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        /*need data for email*/
    }

    @Async("asyncExecutor")
    public void notifySlaBreached(IncidentEmailVO incidentEmailVO) {
        /*need data for email*/
        List<Module> modules = moduleService.getModuleByIds(Arrays.asList(incidentEmailVO.getModuleId(), incidentEmailVO.getSubModuleId()));
        String category = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        String subCategory = modules.stream().filter(i -> {
            return i.getId().equals(incidentEmailVO.getModuleId());
        }).findFirst().get().getName();
        List<UserVO> userVOS = userService.getUsersByRoles(
                Arrays.asList("ORG_INCIDENT_AGENT_LEAD", "ORG_INCIDENT_AGENT_MANAGER", category, subCategory)
        );
        List<String> bccList = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.contains("USER") || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                })
                .map(UserVO::getEmail)
                .collect(Collectors.toList());
        bccList.addAll(Arrays.asList(incidentEmailVO.getWatchList().split(",")));
        //incidentEmailVO.setTo(new String[]{loggedInUser.getEmail()});
        incidentEmailVO.setCc(new String[]{});
        incidentEmailVO.setBcc(bccList.toArray(new String[0]));
        incidentEmailVO.setReplyTo(appReplyToEmail);
        incidentEmailVO.setCategory(category);
        incidentEmailVO.setSubCategory(subCategory);
        incidentEmailVO.setUserVOS(userVOS);
        incidentEmailVO.populateEscalationMatrices();
        //agent notifications
        List<UserVO> agentUserVOS = userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.contains("AGENT") || s.equals("ORG_INCIDENT_AGENT_MANAGER") || s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<Notification> userNotifications = new ArrayList<Notification>() {{
            String title = "New Incident [".concat(incidentEmailVO.getIncidentNo()).concat("]");
            String body = incidentEmailVO.getIncidentNo().substring(0, Math.min(incidentEmailVO.getIncidentNo().length(), 50)).concat("...");
            for (UserVO userVO : agentUserVOS) {
                add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
            }
        }};

        //user notification
        List<String> userEmails = new ArrayList<String>() {{
            add(incidentEmailVO.getUserEmail());
        }};
        userEmails.addAll(Arrays.asList(incidentEmailVO.getWatchList().split(",")));
        List<UserVO> users = userService.getUsersByEmails(userEmails);
        String title = "Incident - [".concat(incidentEmailVO.getIncidentNo()).concat("]");
        String body = "Agent will contact you soon...";
        for (UserVO userVO : users) {
            userNotifications.add(new Notification(title, body, new User(userVO.getVersion(), userVO.getId()), Status.PUSHED, NotificationType.INFO));
        }
        //save notifications
        notificationService.save(userNotifications);
        appEmailService.sendIncidentUpdateEmail(incidentEmailVO);
        /*need data for email*/
    }
}