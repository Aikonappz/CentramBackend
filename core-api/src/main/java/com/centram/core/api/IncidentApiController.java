package com.centram.core.api;


import com.centram.common.dto.AllocateAssetDTO;
import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.IncidentService;
import com.centram.domain.Incident;
import com.centram.domain.User;
import com.fasterxml.jackson.annotation.JsonView;

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



@RequestMapping(value = "/api/v1/incident")
@Controller
public class IncidentApiController {

    private static final Logger log = LoggerFactory.getLogger(IncidentApiController.class);

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;


    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/deallocate-asset", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS,MY GROUP INCIDENTS','WRITE,WRITE|SOLVE',authentication.principal)")
    public ResponseEntity<Incident> deallocateAsset( @Valid @RequestBody AllocateAssetDTO body) {
        return new ResponseEntity<Incident>(incidentService.deallocateAsset(body), HttpStatus.OK);
    }



    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS,MY GROUP INCIDENTS','WRITE,WRITE|SOLVE',authentication.principal)")
    public ResponseEntity save( @Valid @RequestBody Incident body) {
        return ResponseEntity.ok(incidentService.save(body));
    }


    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{incidentId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS,MY GROUP INCIDENTS','READ,WRITE|SOLVE|READ',authentication.principal) || @appSecurityUtilityService.hasOrgAdminAccess(authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<Incident> getIncidentById( @PathVariable("incidentId") BigInteger incidentId) {
        return new ResponseEntity<Incident>(incidentService.getIncidentById(incidentId), HttpStatus.OK);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/user", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY ASSET,MY INCIDENTS,MY ASSET REQUEST','READ|SEARCH,READ|SEARCH,READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> getUserIncidents(
             @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
             @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned,
             @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated,
             @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
             @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
             @RequestParam(value = "title", defaultValue = "", required = false) String title,
             @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
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


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/agent", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REQUESTED ASSET,MY GROUP INCIDENTS','SEARCH|READ,SEARCH|READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> getAgentIncidents(
             @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
             @RequestParam(value = "assigned", defaultValue = "-1", required = false) Integer assigned,
             @RequestParam(value = "deallocated", defaultValue = "-1", required = false) Integer deallocated,
             @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
             @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved,
             @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
             @RequestParam(value = "assignedUserId", defaultValue = "", required = false) String assignedUserId,
             @RequestParam(value = "priorityId", defaultValue = "", required = false) String priorityId,
             @RequestParam(value = "moduleId", defaultValue = "", required = false) String moduleId,
             @RequestParam(value = "subModuleId", defaultValue = "", required = false) String subModuleId,
             @RequestParam(value = "title", defaultValue = "", required = false) String title,
             @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(incidentService.getAgentIncidents(
                incidentType,
                assigned,
                deallocated,
                serialNo,
                approved,
                incidentNo,
                moduleId,
                subModuleId,
                priorityId,
                assignedUserId,
                title,
                status,
                pageable
        ), HttpStatus.OK);
    }


    @RequestMapping(value = "/assign/{ids}/{userId}/{comment}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY GROUP INCIDENTS','WRITE|SOLVE|ASSIGN',authentication.principal)")
    public ResponseEntity<Void> assignIncidents(
            @NotNull  @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @NotNull  @PathVariable("userId") BigInteger userId,
            @NotNull  @PathVariable("comment") String comment
    ) {
        incidentService.assignIncidents(ids, userId, comment);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @RequestMapping(value = "/reopen/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY INCIDENTS','WRITE',authentication.principal)")
    public ResponseEntity<Void> reopenIncident(
            @NotNull  @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
             @PathVariable("status") String status
    ) {
        incidentService.reopenIncident(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/asset/pending/approval", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY ASSET','SEARCH|READ',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> getPendingAssetApprovals(
             @RequestParam(value = "incidentNo", defaultValue = "", required = false) String incidentNo,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(incidentService.getPendingAssetApprovals(incidentNo, pageable), HttpStatus.OK);
    }


    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/asset/approval-action", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    @PreAuthorize("@incidentService.hasApprovalPermission(authentication.principal,#body.id)")
    public ResponseEntity<Void> approveAssetRequest( @Valid @RequestBody AssetApprovalDTO body) {
        incidentService.assetApprovalAction(body);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/allocated-assets", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MY ASSET,MY INCIDENTS,MY ASSET REQUEST','READ|SEARCH,READ|SEARCH,READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<Incident>> getUserAllocatedAssets(
             @RequestParam(value = "incidentType", defaultValue = "INCIDENT", required = false) String incidentType,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(incidentService.getUserAllocatedAssets(pageable), HttpStatus.OK);
    }
}