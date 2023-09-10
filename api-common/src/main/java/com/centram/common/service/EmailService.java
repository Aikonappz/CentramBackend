package com.centram.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.mail.from.email}")
    private String fromAddress;

    @Value("${app.mail.from.name}")
    private String fromName;

    @Value("${app.mail.reply.email}")
    private String replyTo;

    @Value("${send.email:true}")
    private Boolean sendEmail;

    public void sendMail(Map<String, Object> mailMap) {
        MimeMessage message = javaMailSender.createMimeMessage();
        String[] bcc = mailMap.containsKey("bcc") ? (String[]) mailMap.get("bcc") : new String[]{};
        String[] cc = mailMap.containsKey("cc") ? (String[]) mailMap.get("cc") : new String[]{};
        String[] to = mailMap.containsKey("to") ? (String[]) mailMap.get("to") : new String[]{};
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setBcc(bcc);
            helper.setCc(cc);
            helper.setFrom((mailMap.containsKey("fromAddress")) ? mailMap.get("fromAddress").toString() : fromAddress, (mailMap.containsKey("fromName")) ? mailMap.get("fromName").toString() : fromName);
            helper.setReplyTo((mailMap.containsKey("replyTo")) ? mailMap.get("replyTo").toString() : replyTo, (mailMap.containsKey("fromName")) ? mailMap.get("fromName").toString() : fromName);
            helper.setSentDate(new Date());
            helper.setTo(to);
            helper.setSubject(mailMap.get("subject").toString());
            helper.setText(mailMap.get("content").toString(), Boolean.TRUE);
            if (mailMap.containsKey("file")) {
                FileSystemResource file = new FileSystemResource(mailMap.get("file").toString());
                helper.addAttachment(file.getFilename(), file);
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailParseException(e);
        }
        // TODO : have to check before deploy
        // sendEmail = false;
        if (to.length > 0 && sendEmail) {
            javaMailSender.send(message);
        }
    }
}