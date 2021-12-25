package com.centram.common.utility;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.vo.PermissionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
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
        List<String> modules = Arrays.asList(moduleName.split(","));
        List<String> actions = Arrays.asList(actionName.split(","));
        List<PermissionVO> permissionVOS = loggedInUser.getModulePermissions();
        List<String> permissibleActions = new ArrayList<String>();
        for (PermissionVO permissionVO : permissionVOS) {
            for (int k = 0; k < modules.size(); k++) {
                if (permissionVO.getModuleName().equalsIgnoreCase(modules.get(k))) {
                    permissibleActions = Arrays.asList(permissionVO.getActionName().split(","));
                    List<String> moduleActions = Arrays.asList(actions.get(k).split("\\|"));
                    if (permissibleActions.stream().anyMatch(element -> moduleActions.contains(element))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
