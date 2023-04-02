package com.centram.core.service;

import com.centram.common.dto.ThirdPartyLoggedInUser;
import com.centram.domain.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ThirdPartyAuthProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(ThirdPartyAuthProvider.class);

    @Autowired
    private OrganisationService organisationService;

    @Transactional(readOnly = true)
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = authentication.getName();
        String apiPassword = authentication.getCredentials().toString();
        Organisation organisation = organisationService.getOrganisationByApiUserAndPassword(apiKey, apiPassword);
        if (organisation != null) {
            return new ThirdPartyLoggedInUser(organisation);
        } else {
            throw new UsernameNotFoundException("API not yet enabled for : " + apiKey);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}