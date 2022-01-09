package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.vo.AdminDashboardVO;
import com.centram.common.vo.OrgAdminDashboardVO;
import com.centram.common.vo.UserDashboardVO;
import com.centram.core.service.DashboardService;
import com.centram.domain.Department;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "dashboard", description = "Dashboard API")
@RequestMapping(value = "/api/v1/dashboard")
@Controller
public class DashboardApiController {

    private static final Logger log = LoggerFactory.getLogger(DashboardApiController.class);
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private DashboardService dashboardService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "app admin dashboard", nickname = "appAdminDashboard", notes = "app admin dashboard", response = AdminDashboardVO.class, tags = {"dashboard",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class)
    })
    @RequestMapping(value = "/app-admin", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal) && (@appSecurityUtilityService.hasAppAdminAccess(authentication.principal) || @appSecurityUtilityService.hasBusinessLeadAccess(authentication.principal))")
    public ResponseEntity<AdminDashboardVO> appAdminDashboard() {
        return new ResponseEntity<AdminDashboardVO>(dashboardService.appAdminDashboard(), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "org admin dashboard", nickname = "orgAdminDashboard", notes = "org admin dashboard", response = OrgAdminDashboardVO.class, tags = {"dashboard",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class)
    })
    @RequestMapping(value = "/org-admin", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<OrgAdminDashboardVO> orgAdminDashboard() {
        return new ResponseEntity<OrgAdminDashboardVO>(dashboardService.orgAdminDashboard(), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "user dashboard", nickname = "userDashboard", notes = "org admin dashboard", response = Department.class, tags = {"dashboard",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class)
    })
    @RequestMapping(value = "/user", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<UserDashboardVO> userDashboard(
            @ApiParam(value = "Current date", defaultValue = "", required = true)
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<UserDashboardVO>(dashboardService.userDashboard(currentDate), HttpStatus.OK);
    }

}
