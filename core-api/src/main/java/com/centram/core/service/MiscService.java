package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.utility.Utility;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.UserVO;
import com.centram.domain.Department;
import com.centram.domain.Location;
import com.centram.domain.Role;
import com.centram.domain.User;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MiscService {

    private static final Logger log = LoggerFactory.getLogger(MiscService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppEmailService appEmailService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RoleService roleService;

    @Lazy
    @Autowired
    private UserService userService;

    /**
     * Onboard request mail
     *
     * @param requestDemoDTO
     * @return
     */
    public CommonResponse requestDemo(RequestDemoDTO requestDemoDTO) {
        CommonResponse commonResponse = null;
        appEmailService.sendOnboardRequestMail(requestDemoDTO, new HashMap<>());
        commonResponse = new CommonResponse(Boolean.TRUE, "Request send successfully");
        return commonResponse;
    }

    @Async("asyncExecutor")
    public void saveBulkUploadedData(List<Map<String, String>> dataList) {
        LoggedInUser loggedInUserDTO = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> users = new ArrayList<User>();
        List<UserVO> userVOS = new ArrayList<UserVO>();
        UserVO userVO = new UserVO();
        User user = new User();
        String password = null;
        for (Map<String, String> data : dataList) {
            user = new User();
            if (data.get("FIRST_NAME") == null || data.get("FIRST_NAME").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                user.setFirstName(data.get("FIRST_NAME").trim());
            }
            if (data.get("LAST_NAME") == null || data.get("LAST_NAME").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                user.setLastName(data.get("LAST_NAME").trim());
            }
            if (data.get("EMAIL") == null || data.get("EMAIL").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                User u = userService.getUserByEmail(data.get("EMAIL").trim());
                if (u != null) {
                    continue;
                }
                user.setEmail(data.get("EMAIL").trim());
            }
            if (data.get("CONTACT_NO") == null || data.get("CONTACT_NO").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                user.setContactNo(data.get("CONTACT_NO").trim());
            }
            if (data.get("ROLES") == null || data.get("ROLES").trim().equals("")) {
                //TODO: have to decide
                continue;
            } else {
                List<Role> roles = roleService.getByRoleNames(Arrays.asList(data.get("ROLES").trim().toUpperCase().split(",")));
                if (roles != null && roles.size() > 0) {
                    user.setRoles(roles
                            .stream()
                            .map(Role::getId)
                            .collect(Collectors.toList())
                    );
                } else {
                    continue;
                }
            }
            if (data.get("SEC_CONTACT_NO") == null || data.get("SEC_CONTACT_NO").trim().equals("")) {
                user.setSecContactNo(null);
            } else {
                user.setSecContactNo(data.get("SEC_CONTACT_NO").trim());
            }
            if (data.get("EMP_ID") == null || data.get("EMP_ID").trim().equals("")) {
                user.setEmployeeId(null);
            } else {
                User u = userService.getUserByEmployeeId(data.get("EMP_ID").trim());
                if (u != null) {
                    continue;
                }
                user.setEmployeeId(data.get("EMP_ID").trim());
            }
            if (data.get("PROJECT_CODE") == null || data.get("PROJECT_CODE").trim().equals("")) {
                user.setProjectCode(null);
            } else {
                user.setProjectCode(data.get("PROJECT_CODE").trim());
            }
            if (data.get("MANAGER_ID") == null || data.get("MANAGER_ID").trim().equals("")) {
                user.setManagerId(null);
            } else {
                User u = userService.getUserByEmployeeId(data.get("MANAGER_ID").trim());
                if (u != null) {
                    user.setManagerId(u.getId());
                }
            }
            if (data.get("LOCATION") != null && !data.get("LOCATION").trim().equals("")) {
                Location location = locationService.getByLocationName(data.get("LOCATION").trim());
                if (location != null) {
                    user.setLocation(location);
                }
            }
            if (data.get("DEPARTMENT") != null && !data.get("DEPARTMENT").trim().equals("")) {
                Department department = departmentService.getByDepartmentName(data.get("DEPARTMENT").trim());
                if (department != null) {
                    user.setDepartment(department);
                }
            }
            user.setOrganisation(organisationService.getOrganisationById(loggedInUserDTO.getOrganisationId()));
            password = Utility.getUniqueString(8);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(Status.ACTIVE);
            user.setId(null);
            users.add(user);

            userVO = new UserVO(user);
            userVO.setPassword(password);
            userVOS.add(userVO);

            //System.out.println(data);
        }
        log.info("{}", users);
        if (users.size() > 0) {
            userService.saveUsers(users);
            Map<String, String> mailValues = new HashMap<>();
            for (UserVO uv : userVOS) {
                mailValues = new HashMap<>();
                log.info("email : {}, password: {}", userVO.getEmail(), userVO.getPassword());
                mailValues.put("password", userVO.getPassword());
                appEmailService.sendOnboardMail(userVO, mailValues);
            }
        }
    }
}