package com.centram.core.service;


import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.service.EmailService;
import com.centram.common.vo.IncidentEmailVO;
import com.centram.common.vo.UserVO;
import com.centram.domain.AppConfiguration;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
    @Transactional(readOnly = true)
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
     * Send welcome mail
     *
     * @param userVO
     * @param mailValues
     */
    @Transactional(readOnly = true)
    @Async("asyncExecutor")
    public void sendOnboardMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "WELCOME_ONBOARD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("WELCOME_ONBOARD_EMAIL_TEMPLATE"))
                .findFirst().get();
        String onboardEmailTemplate = appConfiguration.getConfigurationValue();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        //String uuid = UUID.randomUUID().toString();
        //redisTemplate.opsForValue().set(uuid, userVO, Duration.ofHours(24));
        //String confirmLink = appBaseUrl.concat("/app/validate-emaill?uuid=").concat(uuid);
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
        //context.setVariable("confirm_link", confirmLink);
        onboardEmailTemplate = templateEngine.process(onboardEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFirstName());
        context.setVariable("app_url", appBaseUrl);
        context.setVariable("team", fromName);
        context.setVariable("mail_body", onboardEmailTemplate);
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
    @Transactional(readOnly = true)
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

    @Async("asyncExecutor")
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
        String mailSubject = appConfiguration.getConfigurationProperties().get(incidentEmailVO.getNewIncident() ? "newIncidentMailSubject" : "oldIncidentMailSubject").toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        context.setVariable("incident_title", incidentEmailVO.getTitle());
        mailSubject = templateEngine.process(mailSubject, context);

        String referer = incidentEmailVO.getMailToType().equalsIgnoreCase("AGENT") ? "agent-all" : "user";
        String incLink = appBaseUrl.concat("/incident/" + referer + "/edit/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
        String mailBody = appConfiguration.getConfigurationProperties().get(incidentEmailVO.getNewIncident() ? "newIncidentMailBody" : "oldIncidentMailBody").toString();
        context = new Context(Locale.ENGLISH);
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
        //context.setVariable("recipient_name", incidentEmailVO.getUserName());
        context.setVariable("recipient_name", "");
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
        emailService.sendMail(mailMap);
    }

    @Async("asyncExecutor")
    public void sendIncidentAssignEmail(IncidentEmailVO incidentEmailVO) {
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "INCIDENT_ASSIGN_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("INCIDENT_ASSIGN_EMAIL_TEMPLATE"))
                .findFirst().get();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);

        context.setVariable("incident_title", incidentEmailVO.getTitle());
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        mailSubject = templateEngine.process(mailSubject, context);

        String referer = incidentEmailVO.getMailToType().equalsIgnoreCase("AGENT") ? "agent-mine" : "user";
        String incLink = appBaseUrl.concat("/incident/agent-mine/edit/").concat(String.valueOf(incidentEmailVO.getIncidentId()));
        String mailBody = appConfiguration.getConfigurationProperties().get("mailBody").toString();
        context = new Context(Locale.ENGLISH);
        context.setVariable("incident_no", incidentEmailVO.getIncidentNo());
        context.setVariable("incident_assignedto_name", incidentEmailVO.getAgentName());
        context.setVariable("incident_assignedto_email", incidentEmailVO.getAgentEmail());
        context.setVariable("incident_assignedto_contactno", incidentEmailVO.getAgentContactNo());
        context.setVariable("incident_ecalation_1", incidentEmailVO.getEscalation1Email());
        context.setVariable("incident_ecalation_2", incidentEmailVO.getEscalation2Email());
        context.setVariable("inc_link", incLink);
        mailBody = templateEngine.process(mailBody, context);

        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", "");
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
        emailService.sendMail(mailMap);
    }

    /*public CommonResponse confirmEmail(AuthRequestDTO authRequestDTO) {
        CommonResponse commonResponse = null;
        UserVO userVO = (UserVO) redisTemplate.opsForValue().get(authRequestDTO.getUsername());
        if (userVO != null) {
            redisTemplate.delete(authRequestDTO.getUsername());
            String encodedPassword = passwordEncoder.encode(authRequestDTO.getPassword());
            userDao.updatePassword(encodedPassword, userVO.getId());

            String baseEmailTemplate = appConfigRepository.findByConfigurationKey("BASE_EMAIL_TEMPLATE").getConfigurationValue();
            AppConfiguration appConfiguration = appConfigRepository.findByConfigurationKey("RESET_PASSWORD_EMAIL_TEMPLATE");
            String resetPasswordEmailTemplate = appConfiguration.getConfigurationValue();
            String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();

            String uuid = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(uuid, userVO, Duration.ofMinutes(2));
            String link = appBaseUrl.concat("/app/login.html");

            StringTemplateResolver templateResolver = new StringTemplateResolver();
            templateResolver.setTemplateMode(TemplateMode.HTML);
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            Context context = new Context(Locale.ENGLISH);
            context.setVariable("signin_link", link);
            resetPasswordEmailTemplate = templateEngine.process(resetPasswordEmailTemplate, context);
            context = new Context(Locale.ENGLISH);
            context.setVariable("recipient_name", userVO.getFirstName());
            context.setVariable("mail_body", resetPasswordEmailTemplate);
            baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);

            Map<String, Object> mailMap = new HashMap<>();
            List<Contact> contacts = userVO.getContacts()
                    .stream()
                    .filter(contact -> contact.getContactType() == ContactType.EMAIL)
                    .collect(Collectors.toList());

            mailMap.put("to", contacts.stream().filter(contact -> contact.getPrimaryContact() == true).collect(Collectors.toList()).stream().map(Contact::getValue).collect(Collectors.toList()).toArray());
            mailMap.put("cc", contacts.stream().map(Contact::getValue).collect(Collectors.toList()).toArray());
            mailMap.put("subject", mailSubject);
            mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));

            emailService.sendMail(mailMap);
            commonResponse = new CommonResponse(Boolean.TRUE, "RESET_PASSWORD_SUCCESS");
            //commonResponse = new CommonResponse(Boolean.TRUE, link);
            activityLogService.save(new ActivityLog(userVO.getId(), (userVO.getOrganisationId() != null) ? userVO.getOrganisationId() : null, ActivityType.RESET_PASSWORD));
        } else {
            commonResponse = new CommonResponse(Boolean.FALSE, "RESET_PASSWORD_FAILED");
        }
        return commonResponse;
    }*/


}
