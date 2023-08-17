package com.centram.core.service;


import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.service.EmailService;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.ProjectUATVO;
import com.centram.common.vo.UserVO;
import com.centram.domain.AppConfiguration;
import com.centram.domain.Notification;
import com.centram.domain.User;
import com.centram.domain.enumarator.LicenseType;
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
    @Transactional(readOnly = true)
    @Async("asyncExecutor")
    public void sendForgotPasswordMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "FORGOT_PASSWORD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("FORGOT_PASSWORD_EMAIL_TEMPLATE")).findFirst().get();
        String forgotPasswordEmailTemplate = appConfiguration.getConfigurationValue();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String link = appBaseUrl.concat("/reset-password/").concat(mailValues.get("uuid"));
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
        //log.info("EMAIL TITLE: {}", mailSubject);
        //log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
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
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("RESET_PASSWORD_EMAIL_TEMPLATE")).findFirst().get();
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
        //log.info("EMAIL TITLE: {}", mailSubject);
        //log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
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
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("DEMO_REQUEST_EMAIL_TEMPLATE")).findFirst().get();
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
        //log.info("EMAIL TITLE: {}", demoRequestMailSubject);
        //log.info("EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("APP_DEMO_REQUEST_EMAIL_TEMPLATE")).findFirst().get();
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

        //log.info("EMAIL TITLE: {}", adminDemoRequestMailSubject);
        //"EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Async("asyncExecutor")
    public void sendOutBoundAssetUpdateEmail(Map<String, Object> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "OUTBOUND_ASSET_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("OUTBOUND_ASSET_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(mailValues.get("subject")).toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(mailValues.get("body")).toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("ord_no", mailValues.get("ord_no"));
        context.setVariable("ord_status", mailValues.get("ord_status"));
        mailSubject = templateEngine.process(mailSubject, context);
        String assetOrderLink = appBaseUrl.concat("/asset/order/".concat(mailValues.get("approver_index").toString()).concat("/action/")).concat(mailValues.get("order_id").toString());
        context = new Context(Locale.ENGLISH);
        context.setVariable("currency", mailValues.get("currency"));
        context.setVariable("req_date", mailValues.get("req_date"));
        context.setVariable("ord_no", mailValues.get("ord_no"));
        context.setVariable("dept_name", mailValues.get("dept_name"));
        context.setVariable("rent_start_date", mailValues.get("rent_start_date"));
        context.setVariable("rent_end_date", mailValues.get("rent_end_date"));
        context.setVariable("loc_name", mailValues.get("loc_name"));
        context.setVariable("req_name", mailValues.get("req_name"));
        context.setVariable("req_id", mailValues.get("req_id"));
        context.setVariable("req_email", mailValues.get("req_email"));
        context.setVariable("req_cnt_no", mailValues.get("req_cnt_no"));
        context.setVariable("asset_type", mailValues.get("asset_type"));
        context.setVariable("qty", mailValues.get("qty"));
        context.setVariable("agreement_end_date", mailValues.get("agreement_end_date"));
        context.setVariable("total", mailValues.get("totalAmount"));
        context.setVariable("extra", mailValues.get("extra"));
        context.setVariable("limit", mailValues.get("limit"));
        context.setVariable("model", mailValues.get("model"));
        context.setVariable("product_type", mailValues.get("product_type"));
        context.setVariable("in_budget", mailValues.get("in_budget"));
        context.setVariable("purchase_type", mailValues.get("purchase_type"));
        context.setVariable("vendor_name", mailValues.get("vendor_name"));
        context.setVariable("app1_name", mailValues.get("app1_name"));
        context.setVariable("app1_cnt_no", mailValues.get("app1_cnt_no"));
        context.setVariable("approver_1", mailValues.get("approver_1"));
        context.setVariable("app2_name", mailValues.get("app2_name"));
        context.setVariable("app2_cnt_no", mailValues.get("app2_cnt_no"));
        context.setVariable("approver_2", mailValues.get("approver_2"));
        context.setVariable("feedback", mailValues.get("feedback"));
        context.setVariable("ord_status", mailValues.get("ord_status"));
        context.setVariable("existing_agreement", mailValues.get("existing_agreement"));
        context.setVariable("ord_link", assetOrderLink);
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", mailValues.get("recipient_name"));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{mailValues.get("to").toString()});
        mailMap.put("cc", new String[]{});
        mailMap.put("bcc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("ASSET EMAIL TITLE: {}", mailSubject);
        //log.info("ASSET EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        Notification notification = (Notification) mailValues.get("notification");
        notification.setNotificationTitle(mailSubject);
        notification.setNotificationBody(mailBody);
        notificationService.save(notification);
        emailService.sendMail(mailMap);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Async("asyncExecutor")
    public void sendInboundAssetRequestUpdateEmail(Map<String, Object> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "INBOUND_ASSET_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("INBOUND_ASSET_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(mailValues.get("subject")).toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(mailValues.get("body")).toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("ord_status", mailValues.get("ord_status"));
        context.setVariable("ord_no", mailValues.get("ord_no"));
        mailSubject = templateEngine.process(mailSubject, context);
        String link = appBaseUrl.concat("/asset/request/action/").concat(mailValues.get("asset_id").toString());
        context = new Context(Locale.ENGLISH);
        context.setVariable("ord_no", mailValues.get("ord_no"));
        context.setVariable("feedback", mailValues.get("feedback"));
        context.setVariable("ord_status", mailValues.get("ord_status"));
        context.setVariable("comment", mailValues.get("comment"));
        context.setVariable("project", mailValues.get("project"));
        context.setVariable("longTerm", mailValues.get("longTerm"));
        context.setVariable("user", mailValues.get("user"));
        context.setVariable("serialNo", mailValues.get("serialNo"));
        context.setVariable("modelNo", mailValues.get("modelNo"));
        context.setVariable("assetType", mailValues.get("assetType"));
        context.setVariable("productCategory", mailValues.get("productCategory"));
        context.setVariable("ntfy_link", link);
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", mailValues.get("recipient_name"));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{mailValues.get("to").toString()});
        mailMap.put("cc", new String[]{});
        mailMap.put("bcc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("ASSET REQUEST EMAIL TITLE: {}", mailSubject);
        //log.info("ASSET REQUEST EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        Notification notification = (Notification) mailValues.get("notification");
        notification.setNotificationTitle(mailSubject);
        notification.setNotificationBody(mailBody);
        notificationService.save(notification);
        emailService.sendMail(mailMap);
    }

    @Transactional
    //@Async("asyncExecutor")
    public void sendChatInteractionEmail(Map<String, Object> attributes) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "CHAT_INTERACTION_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("CHAT_INTERACTION_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("subject").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get("content").toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("recipientName", attributes.get("recipientName"));
        mailSubject = templateEngine.process(mailSubject, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipientName", attributes.get("recipientName"));
        context.setVariable("moduleName", attributes.get("moduleName"));
        context.setVariable("categoryName", attributes.get("categoryName"));
        context.setVariable("subCategoryName", attributes.get("subCategoryName"));
        context.setVariable("interactions", attributes.get("interactions"));
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", attributes.get("recipientName"));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", attributes.get("to"));
        mailMap.put("cc", attributes.get("cc"));
        mailMap.put("bcc", attributes.get("bcc"));
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("CHAT INTERACTION EMAIL TITLE: {}", mailSubject);
        //log.info("CHAT INTERACTION BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Async("asyncExecutor")
    public void sendIncidentUpdateEmail(IncidentEmailVO incidentEmailVO) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "INCIDENT_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("INCIDENT_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(incidentEmailVO.getMailSubjectKey()).toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(incidentEmailVO.getMailBodyKey()).toString();
        String incLink = null;
        if (incidentEmailVO.getIncidentType() == LicenseType.INCIDENT) {
            if (incidentEmailVO.getMailToType().equalsIgnoreCase("EMP")) {
                incLink = appBaseUrl.concat("/incident/user/edit/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
            } else {
                incLink = appBaseUrl.concat("/incident/agent-all/edit/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
            }
        } else {
            if (incidentEmailVO.getMailToType().equalsIgnoreCase("EMP")) {
                incLink = appBaseUrl.concat("/asset/user/edit/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
            } else if (incidentEmailVO.getMailToType().equalsIgnoreCase("EMP_MNGR")) {
                incLink = appBaseUrl.concat("/asset/request/action/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
            } else {
                incLink = appBaseUrl.concat("/asset/agent-all/edit/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
            }
        }
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        context.setVariable("ord_status", incidentEmailVO.getAssetApproved() ? "Approved" : "Rejected");
        mailSubject = templateEngine.process(mailSubject, context);
        String recipientName = incidentEmailVO.getRecipientName();
        context = new Context(Locale.ENGLISH);
        context.setVariable("ord_status", incidentEmailVO.getAssetApproved() ? "Approved" : "Rejected");
        context.setVariable("ticketType", (incidentEmailVO.getTicketType()));
        context.setVariable("assetValidity", (incidentEmailVO.getAssetValidity()));
        context.setVariable("assetDtl", (incidentEmailVO.getOldAsset()));
        context.setVariable("sl_no", (incidentEmailVO.getSerialNo()));
        context.setVariable("model", (incidentEmailVO.getModelNo()));
        context.setVariable("incident_title", (incidentEmailVO.getTitle()));
        context.setVariable("incident_communication", (incidentEmailVO.getDescription()));
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        context.setVariable("incident_priority", incidentEmailVO.getPriority());
        context.setVariable("incident_sla", incidentEmailVO.getSla());
        context.setVariable("incident_status", incidentEmailVO.getStatus());
        context.setVariable("incident_category", incidentEmailVO.getCategory());
        context.setVariable("incident_subcategory", incidentEmailVO.getSubCategory());
        context.setVariable("incident_raisedby_department", incidentEmailVO.getDepartment());
        context.setVariable("incident_raisedby_ac_name", incidentEmailVO.getUserAccountDetails());
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
        //log.info("INCIDENT/ASSET EMAIL TITLE: {}", mailSubject);
        //log.info("INCIDENT/ASSET EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
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
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("ORGANISATION_UPDATE")).findFirst().get();
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
        //log.info("ORGANISATION UPDATE EMAIL TITLE: {}", mailSubject);
        //log.info("ORGANISATION UPDATE EMAIL BODY: {}", body);
        emailService.sendMail(mailMap);
        if (mailValues.containsKey("userToNotify")) {
            List<UserVO> userVOS = (List<UserVO>) mailValues.get("userToNotify");
            for (UserVO uv : userVOS) {
                notificationService.save(Collections.singletonList(new Notification(mailSubject, mailBody, new User(uv), Status.PUSHED, NotificationType.INFO)));
            }
        }
    }

    @Transactional
    //@Async("asyncExecutor")
    public void assetWarrantyExpiration(Map<String, Object> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "ASSET_WARRANTY_EXPIRATION_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("ASSET_WARRANTY_EXPIRATION_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("subject").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get("content").toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("exp_date", mailValues.get("exp_date"));
        context.setVariable("asset_no", mailValues.get("asset_no"));
        context.setVariable("adminTeamEmail", mailValues.get("adminTeamEmail"));
        mailSubject = StringEscapeUtils.unescapeHtml4(templateEngine.process(mailSubject, context));
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", "Asset Manager,");
        context.setVariable("exp_date", mailValues.get("exp_date"));
        context.setVariable("asset_no", mailValues.get("asset_no"));
        context.setVariable("adminTeamEmail", mailValues.get("adminTeamEmail"));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        String body = StringEscapeUtils.unescapeHtml4(baseEmailTemplate);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", ((List<String>) mailValues.get("recipients")).toArray(new String[0]));
        mailMap.put("bcc", new String[]{});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", body);
        //log.info("ASSET EXPIRATION EMAIL TITLE: {}", mailSubject);
        //log.info("ASSET EXPIRATION EMAIL BODY: {}", body);
        emailService.sendMail(mailMap);
        if (mailValues.containsKey("userToNotify")) {
            List<UserVO> userVOS = (List<UserVO>) mailValues.get("userToNotify");
            for (UserVO uv : userVOS) {
                notificationService.save(Collections.singletonList(new Notification(mailSubject, mailBody, new User(uv), Status.PUSHED, NotificationType.INFO)));
            }
        }
    }

    @Transactional
    //@Async("asyncExecutor")
    public void assetValidityExpiration(Map<String, Object> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "ASSET_VALIDITY_EXPIRATION_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("ASSET_VALIDITY_EXPIRATION_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("subject").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get("content").toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("exp_date", mailValues.get("exp_date"));
        context.setVariable("asset_no", mailValues.get("asset_no"));
        context.setVariable("adminTeamEmail", mailValues.get("adminTeamEmail"));
        mailSubject = StringEscapeUtils.unescapeHtml4(templateEngine.process(mailSubject, context));
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", "Asset Manager,");
        context.setVariable("exp_date", mailValues.get("exp_date"));
        context.setVariable("asset_no", mailValues.get("asset_no"));
        context.setVariable("adminTeamEmail", mailValues.get("adminTeamEmail"));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        String body = StringEscapeUtils.unescapeHtml4(baseEmailTemplate);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", ((List<String>) mailValues.get("recipients")).toArray(new String[0]));
        mailMap.put("bcc", new String[]{});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", body);
        //log.info("ASSET EXPIRATION EMAIL TITLE: {}", mailSubject);
        //log.info("ASSET EXPIRATION EMAIL BODY: {}", body);
        emailService.sendMail(mailMap);
        if (mailValues.containsKey("userToNotify")) {
            List<UserVO> userVOS = (List<UserVO>) mailValues.get("userToNotify");
            for (UserVO uv : userVOS) {
                notificationService.save(Collections.singletonList(new Notification(mailSubject, mailBody, new User(uv), Status.PUSHED, NotificationType.INFO)));
            }
        }
    }


    @Transactional
    //@Async("asyncExecutor")
    public void organisationNotification(Map<String, Object> mailValues, Boolean expired) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "ORGANISATION_UPDATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("ORGANISATION_UPDATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(expired ? "orgExpireSub" : "orgAboutToExpireSub").toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(expired ? "orgExpireBody" : "orgAboutToExpireBody").toString();
        String link = appBaseUrl.concat("/sign-in");
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("link", link);
        context.setVariable("org_name", mailValues.get("org_name"));
        context.setVariable("adminTeamEmail", mailValues.get("adminTeamEmail"));
        context.setVariable("expired_date", mailValues.get("expired_date"));
        mailSubject = StringEscapeUtils.unescapeHtml4(templateEngine.process(mailSubject, context));
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
        //log.info("ORGANISATION NOTIFICATION EMAIL TITLE: {}", mailSubject);
        //log.info("ORGANISATION NOTIFICATION EMAIL BODY: {}", body);
        emailService.sendMail(mailMap);
        if (mailValues.containsKey("userToNotify")) {
            List<UserVO> userVOS = (List<UserVO>) mailValues.get("userToNotify");
            for (UserVO uv : userVOS) {
                notificationService.save(Collections.singletonList(new Notification(mailSubject, mailBody, new User(uv), Status.PUSHED, NotificationType.INFO)));
            }
        }
    }

    /**
     * Send upload result mail
     *
     * @param
     * @param mailValues
     */
    @Transactional
    //@Async("asyncExecutor")
    public void sendUploadResult(Map<String, Object> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "UPLOAD_RESULT_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("UPLOAD_RESULT_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(mailValues.get("mailSubject")).toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(mailValues.get("mailBody")).toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("upload_tp", mailValues.get("upload_tp").toString());
        mailSubject = templateEngine.process(mailSubject, context);
        templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        context = new Context(Locale.ENGLISH);
        context.setVariable("upload_tp", mailValues.get("upload_tp").toString());
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", mailValues.get("recipient_name").toString().concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{mailValues.get("to").toString()});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        if (mailValues.containsKey("file")) {
            mailMap.put("file", mailValues.get("file").toString());
        }
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("UPLOAD RESULT EMAIL TITLE: {}", mailSubject);
        //log.info("UPLOAD RESULT EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
        Boolean hasUploadIssue = (Boolean) mailValues.get("has_issue");
        if (hasUploadIssue) {
            notificationService.save(Collections.singletonList(new Notification(mailSubject, mailValues.get("upload_tp") + " file import has some issues! Please refer your email for reference.", new User((UserVO) mailValues.get("user")), Status.PUSHED, NotificationType.INFO)));
        } else {
            notificationService.save(Collections.singletonList(new Notification(mailSubject, mailBody, new User((UserVO) mailValues.get("user")), Status.PUSHED, NotificationType.INFO)));
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
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("WELCOME_ONBOARD_EMAIL_TEMPLATE")).findFirst().get();
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
        context.setVariable("recipient_name", userVO.getFullName().concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{userVO.getEmail()});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("USER ONBOARD EMAIL TITLE: {}", mailSubject);
        //log.info("USER ONBOARD EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
        notificationService.save(Collections.singletonList(new Notification(mailSubject, mailBody, new User(userVO), Status.PUSHED, NotificationType.INFO)));
    }

    @Transactional
    //@Async("asyncExecutor")
    public void sendBatchReport(Map<String, Object> mailValues) {
        UserVO userVO = (UserVO) mailValues.get("recipient");
        List<String> mailTemplates = Arrays.asList("BASE_EMAIL_TEMPLATE", mailValues.get("mailTemplate").toString());
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(mailTemplates);
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals(mailValues.get("mailTemplate").toString())).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(mailValues.get("mailSubject")).toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(mailValues.get("mailBody")).toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("start", mailValues.get("start"));
        context.setVariable("end", mailValues.get("end"));
        mailSubject = StringEscapeUtils.unescapeHtml4(templateEngine.process(mailSubject, context));
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFullName().concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{userVO.getEmail()});
        mailMap.put("cc", new String[]{});
        mailMap.put("subject", mailSubject);
        mailMap.put("file", mailValues.get("file"));
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("BATCH REPORT EMAIL TITLE: {}", mailSubject);
        //log.info("BATCH REPORT EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
        notificationService.save(Collections.singletonList(new Notification(mailSubject, "Please check your email for details!", new User(userVO), Status.PUSHED, NotificationType.INFO)));
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void notifyUatActivities(ProjectUATVO projectUATVO) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "UAT_ACTIVITIES_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE")).findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream().filter(ac -> ac.getConfigurationKey().equals("UAT_ACTIVITIES_EMAIL_TEMPLATE")).findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get(projectUATVO.getMailSubjectKey()).toString();
        String mailBody = appConfiguration.getConfigurationProperties().get(projectUATVO.getMailBodyKey()).toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH, projectUATVO.getEmailValues());
        mailSubject = templateEngine.process(mailSubject, context);
        context = new Context(Locale.ENGLISH, projectUATVO.getEmailValues());
        mailBody = templateEngine.process(mailBody, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", projectUATVO.getRecipientName().concat(","));
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", mailBody);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", projectUATVO.getTo());
        mailMap.put("cc", projectUATVO.getCc());
        mailMap.put("bcc", projectUATVO.getBcc());
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        //log.info("INCIDENT/ASSET EMAIL TITLE: {}", mailSubject);
        //log.info("INCIDENT/ASSET EMAIL BODY: {}", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        if (projectUATVO.getNotifications() != null && !projectUATVO.getNotifications().isEmpty()) {
            List<Notification> notifications = new ArrayList<Notification>();
            for (Notification notification : projectUATVO.getNotifications()) {
                notification.setNotificationTitle(mailSubject);
                notification.setNotificationBody(mailBody);
                notifications.add(notification);
            }
            notificationService.save(notifications);
        }
        emailService.sendMail(mailMap);
    }

}
