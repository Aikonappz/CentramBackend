package com.centram.core.api;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.IncidentService;
import com.centram.domain.Incident;
import com.centram.domain.User;
import com.fasterxml.jackson.annotation.JsonView;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "Incident", description = "Incident Api")
@RequestMapping(value = "/api/v1/incident")
@Controller
public class IncidentApiController {

    private static final Logger log = LoggerFactory.getLogger(IncidentApiController.class);

    @Autowired
    private IncidentService incidentService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save an incident", nickname = "save", notes = "Save an incident", tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS,MY GROUP INCIDENTS','WRITE,WRITE|SOLVE',authentication.principal)")
    public ResponseEntity<Incident> save(@ApiParam(value = "Incident object", required = true) @Valid @RequestBody Incident body) {
        return new ResponseEntity<Incident>(incidentService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find incident by Id", nickname = "getIncidentById", notes = "Find incident by Id", response = Incident.class, tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = User.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{incidentId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS,MY GROUP INCIDENTS','READ,WRITE|SOLVE|READ',authentication.principal) || @appSecurityUtilityService.hasOrgAdminAccess(authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<Incident> getIncidentById(@ApiParam(value = "id of incident to return", required = true) @PathVariable("incidentId") BigInteger incidentId) {
        return new ResponseEntity<Incident>(incidentService.getIncidentById(incidentId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all user incidents", nickname = "getUserIncidents", notes = "Get all user incidents", response = PaginatedList.class, tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/user", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY ASSET,MY INCIDENTS,MY ASSET REQUEST','READ|SEARCH,READ|SEARCH,READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> getUserIncidents(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Asset Assigned", defaultValue = "-1", required = false) @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned,
            @ApiParam(value = "Asset Deallocated", defaultValue = "-1", required = false) @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated,
            @ApiParam(value = "Serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "Incident no", defaultValue = "", required = false) @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
            @ApiParam(value = "Title", defaultValue = "", required = false) @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(
                incidentService.getUserIncidents(
                        incidentType,
                        assigned,
                        deallocated,
                        serialNo,
                        incidentNo,
                        title,
                        status,
                        pageable
                ), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all agent incidents", nickname = "getAgentIncidents", notes = "Get all agent incidents", response = PaginatedList.class, tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/agent", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REQUESTED ASSET,MY GROUP INCIDENTS','SEARCH|READ,SEARCH|READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> getAgentIncidents(
            @ApiParam(value = "Incident Type", defaultValue = "INCIDENT", required = false) @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
            @ApiParam(value = "Approved Asset", defaultValue = "-1", required = false) @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved,
            @ApiParam(value = "incident no", defaultValue = "", required = false) @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
            @ApiParam(value = "assignedUserId", defaultValue = "", required = false) @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
            @ApiParam(value = "priorityId", defaultValue = "", required = false) @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
            @ApiParam(value = "moduleId", defaultValue = "", required = false) @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
            @ApiParam(value = "subModuleId", defaultValue = "", required = false) @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
            @ApiParam(value = "title", defaultValue = "", required = false) @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(incidentService.getAgentIncidents(incidentType, approved, incidentNo, moduleId, subModuleId, priorityId, assignedUserId, title, status, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Assign agent to Incidents", nickname = "assignIncidents", notes = "Assign agent to Incidents", tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Incident not found")
    })
    @RequestMapping(value = "/assign/{ids}/{userId}/{comment}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY GROUP INCIDENTS','WRITE|SOLVE|ASSIGN',authentication.principal)")
    public ResponseEntity<Void> assignIncidents(
            @NotNull @ApiParam(value = "Incident id's to assign", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @NotNull @ApiParam(value = "User Id", required = true) @PathVariable("userId") BigInteger userId,
            @NotNull @ApiParam(value = "Assign comment", required = true) @PathVariable("comment") String comment
    ) {
        incidentService.assignIncidents(ids, userId, comment);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Reopen an Incident", nickname = "reopenIncident", notes = "Reopen an Incident", tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Incident not found")
    })
    @RequestMapping(value = "/reopen/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS','WRITE',authentication.principal)")
    public ResponseEntity<Void> reopenIncident(
            @NotNull @ApiParam(value = "Incident id's to change", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @ApiParam(value = "status", required = true) @PathVariable("status") String status
    ) {
        incidentService.reopenIncident(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Approve an asset request", nickname = "approveAssetRequest", notes = "Approve an asset request", tags = {"Incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/asset/approval-action", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    @PreAuthorize("@incidentService.hasApprovalPermission(authentication.principal,#body.id)")
    public ResponseEntity<Void> approveAssetRequest(@ApiParam(value = "AssetApprovalDTO object", required = true) @Valid @RequestBody AssetApprovalDTO body) {
        incidentService.assetApprovalAction(body);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}