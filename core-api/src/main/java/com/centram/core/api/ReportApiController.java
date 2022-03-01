package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.ReportService;
import com.centram.domain.Incident;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
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
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentReport(moduleId, subModuleId, priorityId, agingFilter, raisedUserId, assignedUserId, status, allOpen, allClosed, isReopened, start, end, pageable), HttpStatus.OK);
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Raised User Id", defaultValue = "", required = false) @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId,
            @ApiParam(value = "Assigned User Id", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.downloadIncidentReport(moduleId, subModuleId, priorityId, status, start, end));
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentEscalationReport(moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentEscalationReportDownload(moduleId, subModuleId, priorityId, status, start, end));
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentReopenReport(moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentReopenReportDownload(moduleId, subModuleId, priorityId, status, start, end));
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentAgingReport(moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
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
            @ApiParam(value = "Module Id", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "Sub Module Id", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "Priority Id", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "Incident Status", defaultValue = "", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Start Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @ApiParam(value = "End Date Time", defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end
    ) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentAgingReportDownload(moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-reopen-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

}
