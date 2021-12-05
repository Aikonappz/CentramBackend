package com.centram.core.api;


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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "incident", description = "Incident Api")
@RequestMapping(value = "/api/v1/incident")
@Controller
public class IncidentApiController {

    private static final Logger log = LoggerFactory.getLogger(IncidentApiController.class);

    @Autowired
    private IncidentService incidentService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add/Update a user", nickname = "save", notes = "Add/Update a user", tags = {"incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<Incident> save(@ApiParam(value = "Incident object", required = true) @Valid @RequestBody Incident body) {
        return new ResponseEntity<Incident>(incidentService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find incident by Id", nickname = "getIncidentById", notes = "Find incident by Id", response = Incident.class, tags = {"incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "incident not found")
    })
    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{incidentId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Incident> getIncidentById(@ApiParam(value = "id of incident to return", required = true) @PathVariable("incidentId") BigInteger incidentId) {
        return new ResponseEntity<Incident>(incidentService.getIncidentById(incidentId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all incidents", nickname = "getIncidents", notes = "Get all incidents", response = PaginatedList.class, tags = {"incident",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Incident>> getUsers(
            @ApiParam(value = "User Email", defaultValue = "", required = false) @RequestParam(value = "email", defaultValue = "", required = false) String email,
            @ApiParam(value = "User EmployeeId", defaultValue = "", required = false) @RequestParam(value = "employeeId", defaultValue = "", required = false) String employeeId,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Incident>>(incidentService.getIncidents(pageable), HttpStatus.OK);
    }
}