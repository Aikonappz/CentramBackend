package com.centram.core.api;

import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.OrganisationService;
import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.User;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;


@RequestMapping(value = "/api/v1/organisation")
@Controller
public class OrganisationApiController {

    private static final Logger log = LoggerFactory.getLogger(OrganisationApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private OrganisationService organisationService;


    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORGANIZATION','WRITE',authentication.principal)")
    public ResponseEntity<Organisation> addOrganisation(@Valid @RequestBody Organisation body) {
        return new ResponseEntity<Organisation>(organisationService.save(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/{ids}/{status}", method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORGANIZATION','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateStatus(@NotNull @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @PathVariable("status") Status status) {
        organisationService.updateStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @RequestMapping(value = "/{organisationId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORGANIZATION','READ',authentication.principal)")
    public ResponseEntity<Organisation> getOrganisationById(@PathVariable("organisationId") BigInteger organisationId) {
        return new ResponseEntity<Organisation>(organisationService.getOrganisationById(organisationId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORGANIZATION','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Organisation>> getOrganisations(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @RequestParam(value = "licenseType", defaultValue = "ALL", required = false) String licenseType, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Organisation>>(organisationService.getOrganisations(name, Status.valueOf(status), LicenseType.valueOf(licenseType), pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/get-settings", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasOrgAdminAccess(authentication.principal)")
    public ResponseEntity<Setting> getOrganisationSettings() {
        return new ResponseEntity<Setting>(organisationService.getOrganisationSettings(), HttpStatus.OK);
    }


    @RequestMapping(value = "/set-settings", produces = {"application/json"}, consumes = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasOrgAdminAccess(authentication.principal)")
    public ResponseEntity<Setting> updateOrganisationSettings(@Valid @RequestBody Setting body) {
        return new ResponseEntity<Setting>(organisationService.updateOrganisationSettings(body), HttpStatus.OK);
    }

    /*@ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload organisation logo", nickname = "uploadOrganisationLogo", notes = "Upload organisation logo", tags = {"Organisation",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "Organisation not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/upload-logo", produces = {"application/json"}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasOrgAdminAccess(authentication.principal)")
    public ResponseEntity<OrganisationDTO> uploadOrganisationLogo(HttpServletRequest request) {
        return new ResponseEntity<OrganisationDTO>(organisationService.uploadOrganisationLogo(request), HttpStatus.OK);
    }*/
}