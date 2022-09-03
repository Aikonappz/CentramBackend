package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.ReportService;
import com.centram.domain.*;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.centram.domain.enumarator.VendorType;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "Report", description = "Report API")
@RequestMapping(value = "/api/v1/report")
@Controller
public class ReportApiController {

    private static final Logger log = LoggerFactory.getLogger(ReportApiController.class);
    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;
    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;
    @Autowired
    private ReportService reportService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "organisation report", nickname = "organisationReport", notes = "organisation report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/organisation", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('SITE ADMIN REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Organisation>> organisationReport(
            @ApiParam(value = "Organisation Name", defaultValue = "", required = false) @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "License Type", defaultValue = "ALL_TYPE", required = false) @RequestParam(value = "licenseType", defaultValue = "ALL_TYPE", required = false) String licenseType,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Organisation>>(reportService.organisationReport(name, Status.valueOf(status), LicenseType.valueOf(licenseType), pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "organisation report download", nickname = "organisationReportDownload", notes = "organisation report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/organisation/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('SITE ADMIN REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> organisationReportDownload(
            @ApiParam(value = "Organisation Name", defaultValue = "", required = false) @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "License Type", defaultValue = "ALL_TYPE", required = false) @RequestParam(value = "licenseType", defaultValue = "ALL_TYPE", required = false) String licenseType
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.downloadOrganisationReport(name, Status.valueOf(status), LicenseType.valueOf(licenseType)));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "organization-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident report", nickname = "incidentReport", notes = "incident report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD,INCIDENT REPORT','READ,READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentReport(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Aging Filter", defaultValue = "", required = false) @RequestParam(value = "agingFilter", defaultValue = "", required = false) String agingFilter,
            @ApiParam(value = "Raised User Id", defaultValue = "", required = false) @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId,
            @ApiParam(value = "Assigned User Id", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "allOpen", defaultValue = "", required = false) @RequestParam(value = "allOpen", defaultValue = "false", required = false) Boolean allOpen,
            @ApiParam(value = "allClosed", defaultValue = "", required = false) @RequestParam(value = "allClosed", defaultValue = "false", required = false) Boolean allClosed,
            @ApiParam(value = "isReopened", defaultValue = "", required = false) @RequestParam(value = "isReopened", defaultValue = "false", required = false) Boolean isReopened,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentReport(incidentType, moduleId, subModuleId, priorityId, agingFilter, raisedUserId, assignedUserId, status, allOpen, allClosed, isReopened, start, end, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident report download", nickname = "organisationReportDownload", notes = "incident report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/incident/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('INCIDENT REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentReportDownload(
            @ApiParam(value = "Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Raised User Id", defaultValue = "", required = false) @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId,
            @ApiParam(value = "Assigned User Id", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.downloadIncidentReport(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident escalation report", nickname = "incidentEscalationReport", notes = "incident escalation report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident-escalation", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ESCALATION REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentEscalationReport(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentEscalationReport(incidentType, moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident escalation report download", nickname = "incidentEscalationReportDownload", notes = "incident escalation report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/incident-escalation/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ESCALATION REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentEscalationReportDownload(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentEscalationReportDownload(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-escalation-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident reopen report", nickname = "incidentReopenReport", notes = "incident reopen report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident-reopen", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REOPEN REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentReopenReport(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentReopenReport(incidentType, moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident reopen report download", nickname = "incidentReopenReportDownload", notes = "incident reopen report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/incident-reopen/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REOPEN REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentReopenReportDownload(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentReopenReportDownload(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-reopen-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident aging report", nickname = "incidentReopenReport", notes = "incident aging report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident-aging", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('AGING REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentAgingReport(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentAgingReport(incidentType, moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "incident aging report download", nickname = "incidentAgingReportDownload", notes = "incident aging report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/incident-aging/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REOPEN REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentAgingReportDownload(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentAgingReportDownload(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-reopen-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "vendor report", nickname = "vendorReport", notes = "vendor report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/vendor", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Vendor>> vendorReport(
            @ApiParam(value = "Vendor Type", defaultValue = "", required = false) @RequestParam(value = "vendorType", defaultValue = "INCIDENT", required = false) String vendorType,
            @ApiParam(value = "In House Vendor", defaultValue = "", required = false) @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Vendor>>(reportService.vendorReport(inHouse, VendorType.valueOf(vendorType), pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "vendor report download", nickname = "vendorReportDownload", notes = "vendor report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/vendor/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> vendorReportDownload(
            @ApiParam(value = "Vendor Type", defaultValue = "", required = false) @RequestParam(value = "vendorType", defaultValue = "INCIDENT", required = false) String vendorType,
            @ApiParam(value = "In House Vendor", defaultValue = "", required = false) @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.vendorReportDownload(inHouse, VendorType.valueOf(vendorType)));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "vendor-" + vendorType + "-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "order report", nickname = "orderReport", notes = "order report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/order", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<AssetOrder>> orderReport(
            @ApiParam(value = "order no", defaultValue = "", required = false) @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<AssetOrder>>(reportService.getOrderedAssetsForReport(orderNo, status, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "order report download", nickname = "orderReportDownload", notes = "order report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/order/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> orderReportDownload(
            @ApiParam(value = "order no", defaultValue = "", required = false) @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.orderReportDownload(orderNo, status));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "order-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "asset report", nickname = "assetReport", notes = "asset report", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/asset", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET TICKET REPORT,ASSET ASIGNENT REPORT','READ,READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> assetTicketReport(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Asset Assigned", defaultValue = "-1", required = false) @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned,
            @ApiParam(value = "Asset Deallocated", defaultValue = "-1", required = false) @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated,
            @ApiParam(value = "Serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "Approved Asset", defaultValue = "-1", required = false) @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved,
            @ApiParam(value = "incident no", defaultValue = "", required = false) @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
            @ApiParam(value = "assignedUserId", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "raisedUserId", defaultValue = "", required = false) @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId,
            @ApiParam(value = "priorityId", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "moduleId", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "subModuleId", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "title", defaultValue = "", required = false) @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.assetTicketReport(
                incidentType, assigned, deallocated, serialNo, approved,
                incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId,
                title, status, pageable
        ), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "asset ticket report download", nickname = "assetTicketReportDownload", notes = "asset ticket report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/asset/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET TICKET REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> assetTicketReportDownload(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Asset Assigned", defaultValue = "-1", required = false) @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned,
            @ApiParam(value = "Asset Deallocated", defaultValue = "-1", required = false) @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated,
            @ApiParam(value = "Serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "Approved Asset", defaultValue = "-1", required = false) @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved,
            @ApiParam(value = "incident no", defaultValue = "", required = false) @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
            @ApiParam(value = "assignedUserId", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "raisedUserId", defaultValue = "", required = false) @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId,
            @ApiParam(value = "priorityId", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "moduleId", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "subModuleId", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "title", defaultValue = "", required = false) @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.assetTicketReportDownload(
                incidentType, assigned, deallocated, serialNo, approved, incidentNo,
                moduleId, subModuleId, priorityId, assignedUserId,raisedUserId, title, status
        ));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "asset-ticket-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "asset assignment report download", nickname = "assetAssignmentReportDownload", notes = "asset assignment report download", response = PaginatedList.class, tags = {"Report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/asset/assignment/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET ASIGNENT REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> assetAssignmentReportDownload(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Asset Assigned", defaultValue = "-1", required = false) @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned,
            @ApiParam(value = "Asset Deallocated", defaultValue = "-1", required = false) @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated,
            @ApiParam(value = "Serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "Approved Asset", defaultValue = "-1", required = false) @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved,
            @ApiParam(value = "incident no", defaultValue = "", required = false) @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
            @ApiParam(value = "assignedUserId", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "raisedUserId", defaultValue = "", required = false) @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId,
            @ApiParam(value = "priorityId", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "moduleId", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "subModuleId", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "title", defaultValue = "", required = false) @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.assetAssignmentReportDownload(
                incidentType, assigned, deallocated, serialNo, approved, incidentNo,
                moduleId, subModuleId, priorityId, assignedUserId,raisedUserId, title, status
        ));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "asset-assignment-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

}