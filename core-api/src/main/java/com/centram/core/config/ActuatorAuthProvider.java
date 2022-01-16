package com.centram.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ActuatorAuthProvider implements AuthenticationProvider {

    @Value("${app.actuator.username}")
    public String username;

    @Value("${app.actuator.password}")
    public String password;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        UsernamePasswordAuthenticationToken token = null;
        if (userName.equals(username) && password.equals(password)) {
            token = new UsernamePasswordAuthenticationToken(userName, password, Collections.singleton(new SimpleGrantedAuthority("DEV-USER")));
        } else {
            throw new UsernameNotFoundException("INVALID CREDENTIAL");
        }
        return token;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
