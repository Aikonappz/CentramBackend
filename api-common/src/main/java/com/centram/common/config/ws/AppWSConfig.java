package com.centram.common.config.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@EnableWebSocketMessageBroker
@Configuration
public class AppWSConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.base.origin.url}")
    private String appBaseOriginUrl;

    @Value("${app.ws.broker.prefix}")
    private String appWSBrokerPrefix;

    @Value("${app.ws.destination.prefix}")
    private String appWSDestinationPrefix;

    @Value("${app.ws.endpoint}")
    private String appWSEndPoint;

    @Autowired
    private AppWSChannelInterceptor appWSChannelInterceptor;

    @Autowired
    private AppWSHandshakeInterceptor appWSHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(appWSBrokerPrefix);
        config.setApplicationDestinationPrefixes(appWSDestinationPrefix);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ui client will use this to connect to the server
        registry.addEndpoint(appWSEndPoint)
                .setAllowedOrigins(appBaseOriginUrl)
                .addInterceptors(appWSHandshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(appWSChannelInterceptor);
    }
}