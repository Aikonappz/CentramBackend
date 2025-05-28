package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.vo.*;
import com.centram.core.service.DashboardService;
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
import java.util.List;


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

    /**
     *
     * @return
     */
    @RequestMapping(value = "/app-admin", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal) && (@appSecurityUtilityService.hasAppAdminAccess(authentication.principal) || @appSecurityUtilityService.hasBusinessLeadAccess(authentication.principal))")
    public ResponseEntity<AdminDashboardVO> appAdminDashboard() {
        return new ResponseEntity<AdminDashboardVO>(dashboardService.appAdminDashboard(), HttpStatus.OK);
    }


    @RequestMapping(value = "/org-admin", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<OrgAdminDashboardVO> orgAdminDashboard() {
        return new ResponseEntity<OrgAdminDashboardVO>(dashboardService.orgAdminDashboard(), HttpStatus.OK);
    }


    @RequestMapping(value = "/user", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<UserDashboardVO> userDashboard(
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<UserDashboardVO>(dashboardService.userDashboard(currentDate), HttpStatus.OK);
    }


    @RequestMapping(value = "/agent", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<AgentDashboardVO> agentDashboard(
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<AgentDashboardVO>(dashboardService.agentDashboard(currentDate), HttpStatus.OK);
    }


    @RequestMapping(value = "/category-admin", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<CategoryAdminDashboardVO> categoryAdminDashboard(
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<CategoryAdminDashboardVO>(dashboardService.categoryAdminDashboard(currentDate), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<UATDashboardVO> uatDashboard(
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<UATDashboardVO>(dashboardService.uatDashboard(currentDate), HttpStatus.OK);
    }

    /**
     * old one
     * @param currentDate
     * @return
     */
    @RequestMapping(value = "/time-sheet", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<List<TimeSheetDashBoardVO>> timeSheetDashboard(
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<List<TimeSheetDashBoardVO>>(dashboardService.timeSheetDashboard(currentDate), HttpStatus.OK);
    }

    /**
     *
     * @param currentDate
     * @return
     */
    @RequestMapping(value = "/time-sheet-v1", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD','READ',authentication.principal)")
    public ResponseEntity<TimeSheetDashBoardV1VO> timeSheetV1Dashboard(
            @RequestParam(value = "currentDate", defaultValue = "", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate
    ) {
        return new ResponseEntity<TimeSheetDashBoardV1VO>(dashboardService.timeSheetV1Dashboard(currentDate), HttpStatus.OK);
    }
}
