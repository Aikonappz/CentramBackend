package com.centram.common.service;


import java.util.HashMap;
import java.util.Map;

//@EnableScheduling
//@Service
class InboxReaderImap {

    /*private static final Logger log = LoggerFactory.getLogger(InboxReaderImap.class);
    @Value("${support.mail.username}")
    String username;
    @Value("${support.mail.password}")
    String password;
    @Value("${support.mail.host}")
    String server;
    @Value("${support.mail.port}")
    String port;
    Properties properties = new Properties();
    Folder sentFolder;
    Store store;

    @PostConstruct
    public void setup() throws MessagingException {
        properties.put("mail.imap.host", server);
        properties.put("mail.imap.port", port);
        properties.put("mail.store.protocol", "imaps");
        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore("imaps");
        store.connect(server, username, password);
        sentFolder = store.getFolder("INBOX");
        sentFolder.open(Folder.READ_ONLY);
        log.info("Inbox Type: ${sentFolder.getType()}");
        log.info("Folders: ${store.getDefaultFolder().list(\"*\")}");
    }

    @Scheduled(fixedDelay = 1000)
    void read() throws MessagingException {
        Message[] messages = sentFolder.getMessages();
        log.info("messages.length---" + messages.length);
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            log.info("--------------------------------");
            log.info("Email Number " + (i + 1));
            log.info("From: " + message.getFrom()[0]);
            log.info("Subject: " + message.getSubject());
        }
        sentFolder.close(false);
    }*/

    public static void main(String[] args) {
        Map<String,Object> dataAttributes = new HashMap<String,Object>();
        String myString = "Category: IT Support\n" +
                "Sub Category: Mouse\n" +
                "Priority: P4\n" +
                "Title: My Desktop Mouse Not Working.\n" +
                "Description: <p>Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. \n" +
                "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software \n" +
                "like Aldus PageMaker including versions of Lorem Ipsum.<p>";

        String[] lines = myString.split(System.getProperty("line.separator"));
        String[] line;
        for(String s: lines){
            line = s.split(":");
            dataAttributes.put(line[0], line[1].trim());
        }

        System.out.println(dataAttributes);
    }

}
