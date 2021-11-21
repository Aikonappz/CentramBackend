package com.centram.core.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {
    private static final Logger log = LoggerFactory.getLogger(FirebaseMessagingService.class);

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public String sendNotification(com.centram.domain.Notification note, String token) throws FirebaseMessagingException {
        Notification notification = Notification
                .builder()
                .setTitle(note.getNotificationTitle())
                .setBody(note.getNotificationBody())
                .build();
        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                //.putAllData(note.getData())
                //.putAllData(note.getNotificationBody())
                .build();
        return firebaseMessaging.send(message);
    }
}
