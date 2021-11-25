package com.centram.core.api;

import com.centram.common.dto.OrganisationDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.OrganisationService;
import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.User;
import com.centram.domain.enumarator.Status;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "organisation", description = "Organisation API")
@RequestMapping(value = "/api/v1/organisation")
@Controller
public class OrganisationApiController {

    private static final Logger log = LoggerFactory.getLogger(OrganisationApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private OrganisationService organisationService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add an organisation", nickname = "addOrganisation", notes = "Add an organisation", tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Organisation> addOrganisation(@ApiParam(value = "Organisation object", required = true) @Valid @RequestBody Organisation body) {
        return new ResponseEntity<Organisation>(organisationService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update an organisation", nickname = "updateOrganisation", notes = "Update an organisation", tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "Organisation not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Organisation> updateOrganisation(@ApiParam(value = "Organisation object", required = true) @Valid @RequestBody Organisation body) {
        return new ResponseEntity<Organisation>(organisationService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of an organisation", nickname = "updateStatus", notes = "Update status of an organisation", tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Organisation not found")
    })
    @RequestMapping(value = "/{ids}/{status}", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Void> updateStatus(@NotNull @ApiParam(value = "Organisation id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Status", required = true) @PathVariable("status") Status status) {
        organisationService.updateStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find organisation by Id", nickname = "getOrganisationById", notes = "Find organisation by Id", response = Organisation.class, tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/{organisationId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Organisation> getOrganisationById(@ApiParam(value = "id of organisation", required = true) @PathVariable("organisationId") BigInteger organisationId) {
        return new ResponseEntity<Organisation>(organisationService.getOrganisationById(organisationId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload organisation logo", nickname = "uploadOrganisationLogo", notes = "Upload organisation logo", tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "Organisation not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/upload-logo", produces = {"application/json"}, method = RequestMethod.POST)
    //@PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<OrganisationDTO> uploadOrganisationLogo(HttpServletRequest request) {
        return new ResponseEntity<OrganisationDTO>(organisationService.uploadOrganisationLogo(request), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all organisation", nickname = "getOrganisations", notes = "Get all Organisation", response = PaginatedList.class, tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<Organisation>> getOrganisations(
            @ApiParam(value = "Organisation Name", defaultValue = "", required = false) @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Organisation>>(organisationService.getOrganisations(name, Status.valueOf(status), pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get organisation settings", nickname = "getOrganisationSettings", notes = "Get organisation settings", response = Setting.class, tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/get-settings", produces = {"application/json"}, method = RequestMethod.GET)
    //@PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Setting> getOrganisationSettings() {
        return new ResponseEntity<Setting>(organisationService.getOrganisationSettings(), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update organisation settings", nickname = "updateOrganisationSettings", notes = "Update organisation settings", tags = {"organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "Organisation not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/set-settings", produces = {"application/json"}, consumes = {"application/json"}, method = RequestMethod.PUT)
    //@PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Setting> updateOrganisationSettings(@ApiParam(value = "Setting object", required = true) @Valid @RequestBody Setting body) {
        return new ResponseEntity<Setting>(organisationService.updateOrganisationSettings(body), HttpStatus.OK);
    }
}