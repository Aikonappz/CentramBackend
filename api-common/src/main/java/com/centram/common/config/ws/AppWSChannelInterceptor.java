package com.centram.common.config.ws;

import com.centram.common.utility.Utility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AppWSChannelInterceptor implements ChannelInterceptor {

    @Value("${app.ws.username.key}")
    private String appWsUsernameKey;

    @Value("${app.ws.password.key}")
    private String appWsPasswordKey;

    @Value("${app.ws.username}")
    private String appWsUsername;

    @Value("${app.ws.password}")
    private String appWsPassword;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        /*MessageHeaders headers = message.getHeaders();
        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        for (Map.Entry<String, List<String>> head : multiValueMap.entrySet()) {
            System.out.println(head.getKey() + "#" + head.getValue());
        }*/
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String username = Utility.decode(accessor.getFirstNativeHeader(appWsUsernameKey));
            final String password = Utility.decode(accessor.getFirstNativeHeader(appWsPasswordKey));
            final UsernamePasswordAuthenticationToken user = getAuthenticatedOrFail(username, password);
            accessor.setUser(user);
        }
        return message;
    }

    private UsernamePasswordAuthenticationToken getAuthenticatedOrFail(final String username, final String password) throws AuthenticationException {
        if (StringUtils.isBlank(username)) {
            throw new AuthenticationCredentialsNotFoundException("Username was null or empty.");
        }
        if (StringUtils.isBlank(password)) {
            throw new AuthenticationCredentialsNotFoundException("Password was null or empty.");
        }
        // Add your own logic for retrieving user in fetchUserFromDb()
        if (!username.equals(appWsUsername) || !password.equals(appWsPassword)) {
            throw new BadCredentialsException("Bad credentials for user " + username);
        }
        // null credentials, we do not pass the password along
        return new UsernamePasswordAuthenticationToken(username, null, Collections.singleton((GrantedAuthority) () -> "USER"));
    }
}