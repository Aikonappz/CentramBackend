package com.centram.core.api;


import com.centram.common.dto.UatScriptReportDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.ReportService;
import com.centram.domain.AssetOrder;
import com.centram.domain.Incident;
import com.centram.domain.Organisation;
import com.centram.domain.Vendor;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.centram.domain.enumarator.Technology;
import com.centram.domain.enumarator.VendorType;
import com.fasterxml.jackson.annotation.JsonView;
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

import java.math.BigInteger;
import java.time.LocalDateTime;


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


    @RequestMapping(value = "/organisation", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('SITE ADMIN REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Organisation>> organisationReport(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @RequestParam(value = "licenseType", defaultValue = "ALL", required = false) String licenseType, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Organisation>>(reportService.organisationReport(name, Status.valueOf(status), LicenseType.valueOf(licenseType), pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/organisation/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('SITE ADMIN REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> organisationReportDownload(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @RequestParam(value = "licenseType", defaultValue = "ALL", required = false) String licenseType) {
        final InputStreamResource resource = new InputStreamResource(reportService.downloadOrganisationReport(name, Status.valueOf(status), LicenseType.valueOf(licenseType)));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "organization-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DASHBOARD,INCIDENT REPORT','READ,READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentReport(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "agingFilter", defaultValue = "", required = false) String agingFilter, @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId, @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @RequestParam(value = "allOpen", defaultValue = "false", required = false) Boolean allOpen, @RequestParam(value = "allClosed", defaultValue = "false", required = false) Boolean allClosed, @RequestParam(value = "isReopened", defaultValue = "false", required = false) Boolean isReopened, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentReport(incidentType, moduleId, subModuleId, priorityId, agingFilter, raisedUserId, assignedUserId, status, allOpen, allClosed, isReopened, start, end, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/incident/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('INCIDENT REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentReportDownload(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId, @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end) {
        final InputStreamResource resource = new InputStreamResource(reportService.downloadIncidentReport(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident-escalation", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ESCALATION REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentEscalationReport(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentEscalationReport(incidentType, moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/incident-escalation/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ESCALATION REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentEscalationReportDownload(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentEscalationReportDownload(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-escalation-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident-reopen", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REOPEN REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentReopenReport(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentReopenReport(incidentType, moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/incident-reopen/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REOPEN REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentReopenReportDownload(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentReopenReportDownload(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-reopen-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/incident-aging", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('AGING REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> incidentAgingReport(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.incidentAgingReport(incidentType, moduleId, subModuleId, priorityId, status, start, end, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/incident-aging/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REOPEN REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> incidentAgingReportDownload(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "status", defaultValue = "", required = false) String status, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end) {
        final InputStreamResource resource = new InputStreamResource(reportService.incidentAgingReportDownload(incidentType, moduleId, subModuleId, priorityId, status, start, end));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "incident-reopen-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/vendor", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Vendor>> vendorReport(@RequestParam(value = "vendorType", defaultValue = "INCIDENT", required = false) String vendorType, @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Vendor>>(reportService.vendorReport(inHouse, VendorType.valueOf(vendorType), pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/vendor/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> vendorReportDownload(@RequestParam(value = "vendorType", defaultValue = "INCIDENT", required = false) String vendorType, @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        final InputStreamResource resource = new InputStreamResource(reportService.vendorReportDownload(inHouse, VendorType.valueOf(vendorType)));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "vendor-" + vendorType + "-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/order", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER REPORT','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<AssetOrder>> orderReport(@RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo, @RequestParam(value = "status", defaultValue = "", required = false) String status, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<AssetOrder>>(reportService.getOrderedAssetsForReport(orderNo, status, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/order/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> orderReportDownload(@RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo, @RequestParam(value = "status", defaultValue = "", required = false) String status) {
        final InputStreamResource resource = new InputStreamResource(reportService.orderReportDownload(orderNo, status));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "order-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/asset", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET TICKET REPORT,ASSET ASSIGNMENT REPORT','READ,READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> assetTicketReport(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned, @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated, @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo, @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved, @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo, @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId, @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "title", defaultValue = "", required = false) String title, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Incident>>(reportService.assetTicketReport(incidentType, assigned, deallocated, serialNo, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId, title, status, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/asset/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET TICKET REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> assetTicketReportDownload(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned, @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated, @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo, @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved, @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo, @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId, @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "title", defaultValue = "", required = false) String title, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status) {
        final InputStreamResource resource = new InputStreamResource(reportService.assetTicketReportDownload(incidentType, assigned, deallocated, serialNo, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId, title, status));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "asset-ticket-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }


    @RequestMapping(value = "/asset/assignment/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET ASSIGNMENT REPORT','READ',authentication.principal)")
    public ResponseEntity<Resource> assetAssignmentReportDownload(@RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType, @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned, @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated, @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo, @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved, @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo, @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId, @RequestParam(value = "raisedUserId", defaultValue = "", required = false) String raisedUserId, @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId, @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId, @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId, @RequestParam(value = "title", defaultValue = "", required = false) String title, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status) {
        final InputStreamResource resource = new InputStreamResource(reportService.assetAssignmentReportDownload(incidentType, assigned, deallocated, serialNo, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, raisedUserId, title, status));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "asset-assignment-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }

    @RequestMapping(value = "/uat", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REPORT,UAT REPORT','READ,READ',authentication.principal)")
    public ResponseEntity<PaginatedList<UatScriptReportDTO>> uatReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @RequestParam(value = "technology", defaultValue = "SAP_SUCCESS_FACTORS", required = false) String technology,
            @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @RequestParam(value = "projectId", defaultValue = "", required = false) String projectId,
            @RequestParam(value = "projectUatId", defaultValue = "", required = false) String projectUatId,
            @RequestParam(value = "projectUatScriptId", defaultValue = "", required = false) String projectUatScriptId,
            @RequestParam(value = "uploadedByUserId", defaultValue = "",  required = false) String uploadedByUserId,
            @RequestParam(value = "status", defaultValue = "",  required = false) String status,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<UatScriptReportDTO>>(reportService.uatReport( start,  end,  Technology.valueOf(technology),  moduleId,  subModuleId,  projectId,  projectUatId,  projectUatScriptId,  uploadedByUserId, status,  pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/uat/download", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REPORT,UAT REPORT','READ,READ',authentication.principal)")
    public ResponseEntity<Resource> uatReportDownload(
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "start", defaultValue = "", required = false) LocalDateTime start,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(value = "end", defaultValue = "", required = false) LocalDateTime end,
            @RequestParam(value = "technology", defaultValue = "SAP_SUCCESS_FACTORS", required = false) String technology,
            @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @RequestParam(value = "projectId", defaultValue = "", required = false) String projectId,
            @RequestParam(value = "projectUatId", defaultValue = "", required = false) String projectUatId,
            @RequestParam(value = "projectUatScriptId", defaultValue = "", required = false) String projectUatScriptId,
            @RequestParam(value = "uploadedByUserId", defaultValue = "",  required = false) String uploadedByUserId,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {

        final InputStreamResource resource = new InputStreamResource(reportService.uatReportDownload( start,  end,  Technology.valueOf(technology),  moduleId,  subModuleId,  projectId,  projectUatId,  projectUatScriptId,  uploadedByUserId,  pageable));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "uat-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }

}