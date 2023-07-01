package com.centram.common.service;


import java.io.IOException;
        import java.util.Properties;

        import javax.annotation.PostConstruct;
        import javax.mail.Folder;
        import javax.mail.Message;
        import javax.mail.MessagingException;
        import javax.mail.Session;
        import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
        import org.springframework.stereotype.Service;



@Service
public class EmailLookUpService {

    private static final Logger log = LoggerFactory.getLogger(EmailLookUpService.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    Folder emailFolder;
    Store store;
    Properties properties = new Properties();

    @PostConstruct
    void setup() {
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", port);
        //properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);
        Store store = null;
        try {
            store = emailSession.getStore("pop3s");
            store.connect(host, username, password);
            emailFolder = store.getFolder("INBOX");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 5000)
    synchronized void read() throws MessagingException, IOException {
        emailFolder.open(Folder.READ_ONLY);
        Message[] messages = emailFolder.getMessages();
        Message message = null;
        for (int i = 0; i < messages.length; i++) {
            message = messages[i];
            log.info("New Message {} ",message.getSubject());
            //System.out.println(message);
        }
        emailFolder.close();
    }
}
