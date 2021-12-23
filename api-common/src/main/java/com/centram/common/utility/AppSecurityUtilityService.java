package com.centram.common.utility;

import com.centram.common.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AppSecurityUtilityService {

    private static final Logger LOG = LoggerFactory.getLogger(AppSecurityUtilityService.class);

    /**
     * check user is app_admin
     *
     * @param loggedInUser
     * @return
     */
    public Boolean hasAppAdminAccess(LoggedInUser loggedInUser) {
        return (loggedInUser.getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().equals("APP_ADMIN"))
                .count() > 0 && loggedInUser.getAppManager());
    }

    /**
     * check user is org_admin
     *
     * @param loggedInUser
     * @return
     */
    public Boolean hasOrgAdminAccess(LoggedInUser loggedInUser) {
        return (loggedInUser.getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().equals("ORG_ADMIN"))
                .count() > 0 && !loggedInUser.getAppManager());
    }

    /**
     * check a module has certain permission or not
     *
     * @param moduleName
     * @param actionName
     * @param loggedInUser
     * @return
     */
    public Boolean hasPermission(String moduleName, String actionName, LoggedInUser loggedInUser) {
        return loggedInUser.getModulePermissions().stream()
                .filter(i -> {
                    List<String> roles = Arrays.asList(i.getActionName().split(","));
                    return i.getModuleName().equals(moduleName) && roles.contains(actionName);
                })
                .count() > 0;
    }
}
