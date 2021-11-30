package com.centram.core.api.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    /*@Autowired
    private SimpMessagingTemplate template;*/

    /*@MessageMapping("/app")
    @SendTo("/topic/notification/{userId}")
    public NotificationVO notify(Notification notification, @DestinationVariable String userId) throws Exception {
        return new NotificationVO(notification);
    }*/

    /*@MessageMapping("/fleet/{fleetId}/driver/{driverId}")
    public void simple(@DestinationVariable String fleetId, @DestinationVariable String driverId) {
        simpMessagingTemplate.convertAndSend("/topic/fleet/" + fleetId, new Simple(fleetId, driverId));
    }*/
}
