package com.centram.core.service;


import com.centram.common.dto.OnboardRequestDTO;
import com.centram.common.service.EmailService;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.AppConfigRepository;
import com.centram.domain.AppConfiguration;
import com.centram.domain.Contact;
import com.centram.domain.enumarator.ContactType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppEmailService {
    private static final Logger log = LoggerFactory.getLogger(AppEmailService.class);

    @Value("${app.base.url:http://localhost/erp-ui/app}")
    private String appBaseUrl;

    @Autowired
    private AppConfigRepository appConfigRepository;

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
    @Async("asyncExecutor")
    public void sendForgotPasswordMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigRepository.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "FORGOT_PASSWORD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("FORGOT_PASSWORD_EMAIL_TEMPLATE"))
                .findFirst().get();
        String forgotPasswordEmailTemplate = appConfiguration.getConfigurationValue();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String link = appBaseUrl.concat("#/app/reset-password?uuid=").concat(mailValues.get("uuid"));
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("reset_link", link);
        forgotPasswordEmailTemplate = templateEngine.process(forgotPasswordEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFirstName());
        context.setVariable("mail_body", forgotPasswordEmailTemplate);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[] {userVO.getEmail()} );
        mailMap.put("cc", new String[] {userVO.getEmail()});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }

    /**
     * Send welcome mail
     *
     * @param userVO
     * @param mailValues
     */
    @Async("asyncExecutor")
    public void sendOnboardMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigRepository.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "WELCOME_ONBOARD_EMAIL_TEMPLATE"));
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
        String loginLink = appBaseUrl.concat("#/app/sign-in");
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("login_link", loginLink);
        context.setVariable("user_name", userVO.getEmail());
        context.setVariable("password", mailValues.get("password"));
        //context.setVariable("confirm_link", confirmLink);
        onboardEmailTemplate = templateEngine.process(onboardEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", userVO.getFirstName());
        context.setVariable("mail_body", onboardEmailTemplate);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[] {userVO.getEmail()});
        mailMap.put("cc", new String[] {userVO.getEmail()});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }

    /**
     * Send reset password mail
     *
     * @param userVO
     * @param mailValues
     */
    @Async("asyncExecutor")
    public void sendResetPasswordMail(UserVO userVO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigRepository.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "RESET_PASSWORD_EMAIL_TEMPLATE"));
        String baseEmailTemplate = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("BASE_EMAIL_TEMPLATE"))
                .findFirst().get().getConfigurationValue();
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("RESET_PASSWORD_EMAIL_TEMPLATE"))
                .findFirst().get();
        String resetPasswordEmailTemplate = appConfiguration.getConfigurationValue();
        String mailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        String link = appBaseUrl.concat("#/app/sign-in");
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
        mailMap.put("to", new String[] {userVO.getEmail()});
        mailMap.put("cc", new String[] {userVO.getEmail()});
        mailMap.put("subject", mailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
    }

    @Async("asyncExecutor")
    public void sendOnboardRequestMail(OnboardRequestDTO onboardRequestDTO, Map<String, String> mailValues) {
        List<AppConfiguration> appConfigurations = appConfigRepository.getAppConfigurations(Arrays.asList("BASE_EMAIL_TEMPLATE", "DEMO_REQUEST_EMAIL_TEMPLATE", "APP_DEMO_REQUEST_EMAIL_TEMPLATE"));
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
        context.setVariable("recipient_name", onboardRequestDTO.getFirstName());
        context.setVariable("mail_body", demoRequestEmailTemplate);
        baseEmailTemplate = templateEngine.process(baseEmailTemplate, context);
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("to", new String[]{onboardRequestDTO.getEmail()});
        mailMap.put("subject", demoRequestMailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(baseEmailTemplate));
        emailService.sendMail(mailMap);
        appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("APP_DEMO_REQUEST_EMAIL_TEMPLATE"))
                .findFirst().get();
        String adminDemoRequestEmailTemplate = appConfiguration.getConfigurationValue();
        String adminDemoRequestMailSubject = appConfiguration.getConfigurationProperties().get("mailSubject").toString();
        context = new Context(Locale.ENGLISH);
        context.setVariable("requester_name", onboardRequestDTO.getFirstName().concat(" ").concat(onboardRequestDTO.getLastName()));
        context.setVariable("requester_email", onboardRequestDTO.getEmail());
        context.setVariable("requester_phone", onboardRequestDTO.getPhone());
        adminDemoRequestEmailTemplate = templateEngine.process(adminDemoRequestEmailTemplate, context);
        context = new Context(Locale.ENGLISH);
        context.setVariable("recipient_name", "Team");
        context.setVariable("mail_body", adminDemoRequestEmailTemplate);
        adminBaseEmailTemplate = templateEngine.process(adminBaseEmailTemplate, context);
        mailMap = new HashMap<>();
        mailMap.put("to", new String[]{adminTeamEmail});
        mailMap.put("subject", adminDemoRequestMailSubject);
        mailMap.put("content", StringEscapeUtils.unescapeHtml4(adminBaseEmailTemplate));
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
