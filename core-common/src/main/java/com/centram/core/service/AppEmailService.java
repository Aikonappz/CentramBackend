package com.centram.core.service;


import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.service.EmailService;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.UserVO;
import com.centram.domain.AppConfiguration;
import com.centram.domain.Notification;
import com.centram.domain.User;
import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.*;

@Service
public class AppEmailService {
    private static final Logger log = LoggerFactory.getLogger(AppEmailService.class);

    @Value("${app.name}")
    private String appName;

    @Value("${app.base.url}")
    private String appBaseUrl;

    @Value("${app.mail.from.name}")
    private String fromName;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private EmailService emailService;

    @Value("${app.mail.admin.team.email}")
    private String adminTeamEmail;

    @Autowired
    private NotificationService notificationService;

    /**
     * Send forgot password mail
     *
     * @param userVO
     * @param mailValues
     */
    @Transactional
    @Async("asyncExecutor")
    public void sendForgotPasswordMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "FORGOT_PASSWORD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("FORGOT_PASSWORD_EMAIL_TEMPLATE"))
                .findFirst().get();
        String forgotPasswordEmailTemplate = appConfiguration.getConfigurationValue();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String link = appBaseUrl.concat("/reset-password?uuid=").concat(mailValues.get("uuid"));
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("reset_link", link);
        forgotPasswordEmailTemplate = templateEngine.process(forgotPasswordEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFirstName());
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", forgotPasswordEmailTemplate);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{userVO.getEmail()});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        log.info("EMAIL TITLE: {}", mailSubject);
        log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }

    /**
     * Send reset password mail
     *
     * @param userVO
     * @param mailValues
     */
    @Transactional
    @Async("asyncExecutor")
    public void sendResetPasswordMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "RESET_PASSWORD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("RESET_PASSWORD_EMAIL_TEMPLATE"))
                .findFirst().get();
        String resetPasswordEmailTemplate = appConfiguration.getConfigurationValue();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String link = appBaseUrl.concat("/sign-in");
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("signin_link", link);
        resetPasswordEmailTemplate = templateEngine.process(resetPasswordEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFirstName());
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", resetPasswordEmailTemplate);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{userVO.getEmail()});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        log.info("EMAIL TITLE: {}", mailSubject);
        log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }


    /**
     * onboard request mail
     *
     * @param requestDemoDTO
     * @param mailValues
     */
    @Transactional
    @Async("asyncExecutor")
    public void sendOnboardRequestMail(RequestDemoDTO requestDemoDTO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "DEMO_REQUEST_EMAIL_TEMPLATE", "APP_DEMO_REQUEST_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("DEMO_REQUEST_EMAIL_TEMPLATE"))
                .findFirst().get();
        String adminBaseEmailTemplate = baseEmailTemplate;
        String demoRequestEmailTemplate = appConfiguration.getConfigurationValue();
        String demoRequestMailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", requestDemoDTO.getName());
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", demoRequestEmailTemplate);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{requestDemoDTO.getEmail()});
        mailMap.put("subject", demoRequestMailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
        log.info("EMAIL TITLE: {}", demoRequestMailSubject);
        log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("APP_DEMO_REQUEST_EMAIL_TEMPLATE"))
                .findFirst().get();
        String adminDemoRequestEmailTemplate = appConfiguration.getConfigurationValue();
        String adminDemoRequestMailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        context = new Context(Locale.ENGLISH);
        context.setVariable("requester_name", requestDemoDTO.getName());
        context.setVariable("requester_email", requestDemoDTO.getEmail());
        context.setVariable("requester_phone", requestDemoDTO.getPhone());
        adminDemoRequestEmailTemplate = templateEngine.process(adminDemoRequestEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", "Team");
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", adminDemoRequestEmailTemplate);
        adminBaseEmailTemplate = templateEngine.process(adminBaseEmailTemplate, context);
        mailMap = new HashMap<>();
        mailMap.put("to", new String[]{adminTeamEmail});
        mailMap.put("subject", adminDemoRequestMailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(adminBaseEmailTemplate));

        log.info("EMAIL TITLE: {}", adminDemoRequestMailSubject);
        log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Async("asyncExecutor")
    public void sendIncidentUpdateEmail(IncidentEmailVO incidentEmailVO) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(
                Arrays.asList("BASE_EMAIL_TEMPLATE", "INCIDENT_EMAIL_TEMPLATE")
        );
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("INCIDENT_EMAIL_TEMPLATE"))
                .findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("oldMailSubject").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get("oldMailBody").toString();
        String referer = "agent-all";
        String recipientName = incidentEmailVO.getAgentName();
        if (incidentEmailVO.getNewIncident() && incidentEmailVO.getMailToType().equalsIgnoreCase("EMP")) {
            mailSubject = appConfiguration.getConfigurationProperties().get("newEmpMailSubject").toString();
            mailBody = appConfiguration.getConfigurationProperties().get("newEmpMailBody").toString();
            referer = "user";
            recipientName = incidentEmailVO.getUserName();
        } else if (incidentEmailVO.getNewIncident() && incidentEmailVO.getMailToType().equalsIgnoreCase("AGENT_DL")) {
            mailSubject = appConfiguration.getConfigurationProperties().get("newDlMailSubject").toString();
            mailBody = appConfiguration.getConfigurationProperties().get("newDlMailBody").toString();
            recipientName = "DL";
        } else if (!incidentEmailVO.getNewIncident() && incidentEmailVO.getMailToType().equalsIgnoreCase("EMP")) {
            recipientName = incidentEmailVO.getUserName();
        } else if (!incidentEmailVO.getNewIncident() && incidentEmailVO.getMailToType().equalsIgnoreCase("AGENT_DL")) {
            recipientName = "DL";
        }
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        //context.setVariable("incident_title", incidentEmailVO.getTitle());
        mailSubject = templateEngine.process(mailSubject, context);
        String incLink = appBaseUrl.concat("/incident/".concat(referer).concat("/edit/")).concat(String.valueOf(incidentEmailVO.getIncidentId()));
        context = new Context(Locale.ENGLISH);
        context.setVariable("incident_title", (incidentEmailVO.getTitle()));
        context.setVariable("incident_communication", (incidentEmailVO.getDescription()));
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        context.setVariable("incident_priority", incidentEmailVO.getPriority());
        context.setVariable("incident_sla", incidentEmailVO.getSla());
        context.setVariable("incident_status", incidentEmailVO.getStatus());
        context.setVariable("incident_category", incidentEmailVO.getCategory());
        context.setVariable("incident_subcategory", incidentEmailVO.getSubCategory());
        context.setVariable("incident_raisedby_department", incidentEmailVO.getDepartment());
        context.setVariable("incident_raisedby_name", incidentEmailVO.getUserName());
        context.setVariable("incident_raisedby_email", incidentEmailVO.getUserEmail());
        context.setVariable("incident_raisedby_contact", incidentEmailVO.getUserContactNo());
        context.setVariable("incident_assignedto_name", incidentEmailVO.getAgentName());
        context.setVariable("incident_assignedto_email", incidentEmailVO.getAgentEmail());
        context.setVariable("incident_assignedto_contactno", incidentEmailVO.getAgentContactNo());
        context.setVariable("incident_raisedby_location", incidentEmailVO.getUserLocation());
        context.setVariable("incident_watchlist", incidentEmailVO.getWatchList());
        context.setVariable("incident_ecalation_1", incidentEmailVO.getEscalation1Email());
        context.setVariable("incident_ecalation_2", incidentEmailVO.getEscalation2Email());
        context.setVariable("inc_link", incLink);
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", recipientName.concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", incidentEmailVO.getTo());
        mailMap.put("cc", incidentEmailVO.getCc());
        mailMap.put("bcc", incidentEmailVO.getBcc());
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        log.info("INCIDENT EMAIL TITLE: {}", mailSubject);
        log.info("INCIDENT EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        if (incidentEmailVO.getNotifications() != null && incidentEmailVO.getNotifications().size() > 0) {
            List<Notification> notifications = new ArrayList<Notification>();
            for (Notification notification : incidentEmailVO.getNotifications()) {
                notification.setNotificationTitle(mailSubject);
                notification.setNotificationBody(mailBody);
                notifications.add(notification);
            }
            notificationService.save(notifications);
        }
        emailService.sendMail(mailMap);
    }

    @Transactional
    //@Async("asyncExecutor")
    public void sendIncidentAssignEmail(IncidentEmailVO incidentEmailVO) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "INCIDENT_ASSIGN_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("INCIDENT_ASSIGN_EMAIL_TEMPLATE"))
                .findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get("mailBody").toString();
        String referer = "agent-all";
        String recipientName = incidentEmailVO.getAgentName();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        //context.setVariable("incident_title", incidentEmailVO.getTitle());
        mailSubject = templateEngine.process(mailSubject, context);
        String incLink = appBaseUrl.concat("/incident/".concat(referer).concat("/edit/")).concat(String.valueOf(incidentEmailVO.getIncidentId()));
        context = new Context(Locale.ENGLISH);
        context.setVariable("incident_title", (incidentEmailVO.getTitle()));
        context.setVariable("incident_communication", (incidentEmailVO.getDescription()));
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        context.setVariable("incident_priority", incidentEmailVO.getPriority());
        context.setVariable("incident_sla", incidentEmailVO.getSla());
        context.setVariable("incident_status", incidentEmailVO.getStatus());
        context.setVariable("incident_category", incidentEmailVO.getCategory());
        context.setVariable("incident_subcategory", incidentEmailVO.getSubCategory());
        context.setVariable("incident_raisedby_department", incidentEmailVO.getDepartment());
        context.setVariable("incident_raisedby_name", incidentEmailVO.getUserName());
        context.setVariable("incident_raisedby_email", incidentEmailVO.getUserEmail());
        context.setVariable("incident_raisedby_contact", incidentEmailVO.getUserContactNo());
        context.setVariable("incident_assignedto_name", incidentEmailVO.getAgentName());
        context.setVariable("incident_assignedto_email", incidentEmailVO.getAgentEmail());
        context.setVariable("incident_assignedto_contactno", incidentEmailVO.getAgentContactNo());
        context.setVariable("incident_raisedby_location", incidentEmailVO.getUserLocation());
        context.setVariable("incident_watchlist", incidentEmailVO.getWatchList());
        context.setVariable("incident_ecalation_1", incidentEmailVO.getEscalation1Email());
        context.setVariable("incident_ecalation_2", incidentEmailVO.getEscalation2Email());
        context.setVariable("inc_link", incLink);
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", recipientName.concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", incidentEmailVO.getTo());
        mailMap.put("cc", incidentEmailVO.getCc());
        mailMap.put("bcc", incidentEmailVO.getBcc());
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        log.info("ASSIGN EMAIL TITLE: {}", mailSubject);
        log.info("ASSIGN EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        if (incidentEmailVO.getNotifications() != null && incidentEmailVO.getNotifications().size() > 0) {
            List<Notification> notifications = new ArrayList<Notification>();
            for (Notification notification : incidentEmailVO.getNotifications()) {
                notification.setNotificationTitle(mailSubject);
                notification.setNotificationBody(mailBody);
                notifications.add(notification);
            }
            notificationService.save(notifications);
        }
        emailService.sendMail(mailMap);
    }


    @Transactional
    //@Async("asyncExecutor")
    public void organisationUpdate(Map<String, Object> mailValues, Boolean newEntry) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "ORGANISATION_UPDATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("ORGANISATION_UPDATE"))
                .findFirst().get();
        String mailBody = appConfiguration.getConfigurationProperties().get(newEntry ? "newOrgBody" : "oldOrgBody").toString();
        String mailSubject = appConfiguration.getConfigurationProperties().get(newEntry ? "newOrgSub" : "oldOrgSub").toString();
        String link = appBaseUrl.concat("/sign-in");
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("link", link);
        context.setVariable("org_name", mailValues.get("org_name"));
        context.setVariable("valid_from", mailValues.get("valid_from"));
        context.setVariable("valid_to", mailValues.get("valid_to"));
        mailSubject = templateEngine.process(mailSubject, context);
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", mailValues.get("recipientType").toString().concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        String body = StringEscapeUtils.unescapeHtml4(baseEmailTemplate);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", ((List<String>) mailValues.get("recipients")).toArray(new String[0]));
        mailMap.put("bcc", mailValues.get("recipientType").equals("Site Admin") ? new String[]{adminTeamEmail} : new String[]{});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", body);
        log.info("ORGANISATION UPDATE EMAIL TITLE: {}", mailSubject);
        log.info("ORGANISATION UPDATE EMAIL BODY: {}", body);
        emailService.sendMail(mailMap);
        if (mailValues.containsKey("userToNotify")) {
            List<UserVO> userVOS = (List<UserVO>) mailValues.get("userToNotify");
            for (UserVO uv : userVOS) {
                notificationService.save(
                        Collections.singletonList(
                                new Notification(mailSubject, mailBody, new User(uv), Status.PUSHED, NotificationType.INFO)
                        )
                );
            }
        }
    }

    /**
     * Send welcome mail
     *
     * @param userVO
     * @param mailValues
     */
    @Transactional
    //@Async("asyncExecutor")
    public void sendOnboardMail(UserVO userVO, Map<String, Object> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "WELCOME_ONBOARD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("WELCOME_ONBOARD_EMAIL_TEMPLATE"))
                .findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get("mailBody").toString();
        String loginLink = appBaseUrl.concat("/sign-in");
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("login_link", loginLink);
        context.setVariable("app_name", appName);
        context.setVariable("user_name", userVO.getEmail());
        context.setVariable("password", mailValues.get("password"));
        mailSubject = templateEngine.process(mailSubject, context);
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFirstName().concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{userVO.getEmail()});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        log.info("USER ONBOARD EMAIL TITLE: {}", mailSubject);
        log.info("USER ONBOARD EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
        notificationService.save(
                Collections.singletonList(
                        new Notification(mailSubject, mailBody, new User(userVO), Status.PUSHED, NotificationType.INFO)
                )
        );
    }
}
