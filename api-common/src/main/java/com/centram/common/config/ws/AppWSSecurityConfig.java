package com.centram.common.config.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

public class AppWSSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Value("${app.ws.endpoint}")
    private String appWSEndPoint;

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers(appWSEndPoint + "/**").authenticated()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
