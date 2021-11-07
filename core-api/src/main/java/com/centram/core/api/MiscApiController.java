package com.centram.core.api;


import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.vo.CommonResponse;
import com.centram.core.service.DepartmentService;
import com.centram.core.service.LocationService;
import com.centram.core.service.MiscService;
import com.centram.core.service.RoleService;
import com.centram.domain.Department;
import com.centram.domain.Location;
import com.centram.domain.Role;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.math.BigInteger;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "misc", description = "Misc API")
@RequestMapping(value = "/api/v1/misc")
@Controller
public class MiscApiController {

    private static final Logger log = LoggerFactory.getLogger(MiscApiController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MiscService miscService;

    @ApiOperation(value = "Demo Request Api", nickname = "requestDemo", notes = "Demo Request Api", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/request-demo", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> requestDemo(@ApiParam(value = "AuthRequest object", required = true) @Valid @RequestBody RequestDemoDTO body) {
        return new ResponseEntity<CommonResponse>(miscService.requestDemo(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find role by id", nickname = "getRoleById", notes = "Find role by id", response = Role.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Role not found")
    })
    @RequestMapping(value = "/role/{roleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Role> getRoleById(@ApiParam(value = "id of role", required = true) @PathVariable("roleId") BigInteger roleId) {
        return new ResponseEntity<Role>(roleService.getById(roleId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Roles", nickname = "getRoles", notes = "Get all Roles", response = Role.class, responseContainer = "List", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/role/all", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Page<Role>> getRoles(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<Role>>(roleService.getRoles(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find location by id", nickname = "getLocationById", notes = "Find location by id", response = Location.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Location not found")
    })
    @RequestMapping(value = "/location/{locationId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Location> getLocationById(@ApiParam(value = "id of location", required = true) @PathVariable("locationId") BigInteger locationId) {
        return new ResponseEntity<Location>(locationService.getById(locationId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Locations", nickname = "getLocations", notes = "Get all Locations", response = Location.class, responseContainer = "List", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/locations/all", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Page<Location>> getLocations(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<Location>>(locationService.getLocations(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find department by id", nickname = "getDepartentById", notes = "Find department by id", response = Department.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Department not found")
    })
    @RequestMapping(value = "/department/{departmentId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Department> getDepartentById(@ApiParam(value = "id of department", required = true) @PathVariable("departmentId") BigInteger departmentId) {
        return new ResponseEntity<Department>(departmentService.getById(departmentId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all departments", nickname = "getDepartments", notes = "Get all departments", response = Department.class, responseContainer = "List", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/department/all", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Page<Department>> getDepartments(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<Department>>(departmentService.getDepartments(pageable), HttpStatus.OK);
    }
}
