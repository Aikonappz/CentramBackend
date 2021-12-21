package com.centram.common.utility;

import com.centram.common.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppSecurityUtilityService {

    private static final Logger LOG = LoggerFactory.getLogger(AppSecurityUtilityService.class);

    public Boolean hasAppAdminAccess(LoggedInUser loggedInUser) {
        return (loggedInUser.getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().equals("APP_ADMIN"))
                .count() > 0 && loggedInUser.getAppManager());
    }

    public Boolean hasOrgAdminAccess(LoggedInUser loggedInUser) {
        return (loggedInUser.getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().equals("ORG_ADMIN"))
                .count() > 0 && !loggedInUser.getAppManager());
    }
}
