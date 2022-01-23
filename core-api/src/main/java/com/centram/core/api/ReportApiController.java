package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.ReportService;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "report", description = "Report API")
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "organisation report", nickname = "organisationReport", notes = "organisation report", response = PaginatedList.class, tags = {"report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "organisation report download", nickname = "organisationReportDownload", notes = "organisation report download", response = PaginatedList.class, tags = {"report",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
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

}
