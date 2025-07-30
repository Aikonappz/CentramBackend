package com.centram.core.api.ws;

import com.centram.common.vo.NotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /*@MessageMapping("/app")
    @SendTo("/topic/notification/{userId}")
    public NotificationVO notify(Notification notification, @DestinationVariable String userId) throws Exception {
        return new NotificationVO(notification);
    }*/

    /*@MessageMapping("/fleet/{fleetId}/driver/{driverId}")
    public void simple(@DestinationVariable String fleetId, @DestinationVariable String driverId) {
        simpMessagingTemplate.convertAndSend("/topic/fleet/" + fleetId, new Simple(fleetId, driverId));
    }*/

    public void sendNotification(NotificationVO notificationVO) {
        String topic = "/topic/notification/" + notificationVO.getUserId();
        messagingTemplate.convertAndSend(topic, notificationVO);
    }
}
