package com.centram.core.api;


import com.centram.common.dto.PermissionDTO;
import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.common.vo.CommonResponse;
import com.centram.core.service.*;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import com.centram.domain.enumarator.VendorType;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "Misc", description = "Misc API")
@RequestMapping(value = "/api/v1/misc")
@Controller
public class MiscApiController {

    private static final Logger log = LoggerFactory.getLogger(MiscApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DistributionListService distributionListService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PriorityService priorityService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private HolidayCalenderService holidayCalenderService;

    @Autowired
    private MiscService miscService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private AssetModelService assetModelService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @ApiOperation(value = "Request a demo", nickname = "requestADemo", notes = "Request a demo", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/request-demo", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> requestADemo(@ApiParam(value = "AuthRequest object", required = true) @Valid @RequestBody RequestDemoDTO body) {
        return new ResponseEntity<CommonResponse>(miscService.requestDemo(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find module by id", nickname = "getModuleById", notes = "Find module by id", response = Module.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Module.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/module/{moduleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Module> getModuleById(@ApiParam(value = "id of module", required = true) @PathVariable("moduleId") BigInteger moduleId) {
        return new ResponseEntity<Module>(moduleService.getModuleById(moduleId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Modules", nickname = "getModules", notes = "Get all Modules", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-module", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Module>> getModules(
            @NotNull @ApiParam(value = "License Type", defaultValue = "ALL", required = false) @Valid @RequestParam(value = "licenseType", defaultValue = "ALL", required = false) String licenseType,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Module>>(moduleService.getModules(licenseType, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Asset Models", nickname = "getAssetModels", notes = "Get all Asset Models", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-asset-model", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<AssetModel>> getAssetModels(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<List<AssetModel>>(assetModelService.getAssetModels(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find role by id", nickname = "getRoleById", notes = "Find role by id", response = Role.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Role not found")
    })
    @RequestMapping(value = "/role/{roleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Role> getRoleById(@ApiParam(value = "id of role", required = true) @PathVariable("roleId") BigInteger roleId) {
        return new ResponseEntity<Role>(roleService.getById(roleId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Roles", nickname = "getRoles", notes = "Get all Roles", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-role", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Role>> getRoles(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Role>>(roleService.getRoles(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save permission", nickname = "savePermission", notes = "Save permission", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/permission", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PERMISSION','WRITE',authentication.principal)")
    public ResponseEntity<Void> savePermission(@ApiParam(value = "PermissionDTO object", required = true) @Valid @RequestBody PermissionDTO body) {
        permissionService.save(body);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find action by id", nickname = "getActionById", notes = "Find action by id", response = Action.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Role not found")
    })
    @RequestMapping(value = "/action/{actionId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Action> getActionById(@ApiParam(value = "id of action", required = true) @PathVariable("actionId") BigInteger actionId) {
        return new ResponseEntity<Action>(actionService.getById(actionId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Actions", nickname = "getActions", notes = "Get all Actions", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-action", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Action>> getActions(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Action>>(actionService.getActions(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Actions by module and role", nickname = "getActionsByRoleAndModule", notes = "Get all Actions by module and role", response = List.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-action-by-role-module/{roleId}/{moduleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<Action>> getActionsByRoleAndModule(
            @ApiParam(value = "id of role", required = true) @PathVariable("roleId") BigInteger roleId,
            @ApiParam(value = "id of module", required = true) @PathVariable("moduleId") BigInteger moduleId
    ) {
        return new ResponseEntity<List<Action>>(permissionService.getActionsByRoleAndModule(roleId, moduleId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Module By Role", nickname = "getModulesByRole", notes = "Get all Module By Role", response = List.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = List.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-module-by-role/{roleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<Module>> getModulesByRole(@ApiParam(value = "id of role", required = true) @PathVariable("roleId") BigInteger roleId) {
        return new ResponseEntity<List<Module>>(permissionService.getModulesByRole(roleId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find department by id", nickname = "getDepartentById", notes = "Find department by id", response = Department.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Department not found")
    })
    @RequestMapping(value = "/department/{departmentId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','READ',authentication.principal)")
    public ResponseEntity<Department> getDepartentById(@ApiParam(value = "id of department", required = true) @PathVariable("departmentId") BigInteger departmentId) {
        return new ResponseEntity<Department>(departmentService.getById(departmentId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all departments", nickname = "getDepartments", notes = "Get all departments", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-department", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Department>> getDepartments(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Department>>(departmentService.getDepartments(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save a department", nickname = "saveDepartment", notes = "Save a department", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/department", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','WRITE',authentication.principal)")
    public ResponseEntity<Department> saveDepartment(@ApiParam(value = "Department object", required = true) @Valid @RequestBody Department body) {
        return new ResponseEntity<Department>(departmentService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of department's", nickname = "updateStatus", notes = "Update status of department's", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Department not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/department/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateDepartmentsStatus(
            @NotNull @ApiParam(value = "Departent id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @ApiParam(value = "Status", required = true) @PathVariable("status") Status status
    ) {
        departmentService.updateDepartmentsStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save a location", nickname = "saveLocation", notes = "Save a location", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/location", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','READ',authentication.principal)")
    public ResponseEntity<Location> saveLocation(@ApiParam(value = "Location object", required = true) @Valid @RequestBody Location body) {
        return new ResponseEntity<Location>(locationService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of location's", nickname = "updateStatus", notes = "Update status of location's", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Location not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/location/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateLocationsStatus(@NotNull @ApiParam(value = "Location id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Status", required = true) @PathVariable("status") Status status) {
        locationService.updateLocationsStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find location by id", nickname = "getLocationById", notes = "Find location by id", response = Location.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Location not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/location/{locationId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','READ',authentication.principal)")
    public ResponseEntity<Location> getLocationById(@ApiParam(value = "id of location", required = true) @PathVariable("locationId") BigInteger locationId) {
        return new ResponseEntity<Location>(locationService.getById(locationId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Locations", nickname = "getLocations", notes = "Get all Locations", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-location", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Location>> getLocations(
            @ApiParam(value = "id of account", required = false, defaultValue = "") @PathVariable(name = "accountId", required = false) BigInteger accountId,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Location>>(locationService.getLocations(accountId, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save a Priority", nickname = "savePriority", notes = "Save a Priority", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/priority", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','WRITE',authentication.principal)")
    public ResponseEntity<Priority> savePriority(@ApiParam(value = "Priority object", required = true) @Valid @RequestBody Priority body) {
        return new ResponseEntity<Priority>(priorityService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of priorityies", nickname = "updatePrioritiesStatus", notes = "Update status of priorityies", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "priority not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','WRITE',authentication.principal)")
    @RequestMapping(value = "/priority/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    public ResponseEntity<Void> updatePrioritiesStatus(@NotNull @ApiParam(value = "Priority id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Status", required = true) @PathVariable("status") Status status) {
        priorityService.updatePrioritiesStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find priority by id", nickname = "getPriorityById", notes = "Find priority by id", response = Location.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Priority not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/priority/{priorityId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','READ',authentication.principal)")
    public ResponseEntity<Priority> getPriorityById(@ApiParam(value = "id of priority", required = true) @PathVariable("priorityId") BigInteger priorityId) {
        return new ResponseEntity<Priority>(priorityService.getById(priorityId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all priorities", nickname = "getPriorities", notes = "Get all priorities", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-priority", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY,MY INCIDENTS,MY GROUP INCIDENTS,REPORT,','READ,WRITE|SEARCH,WRITE|SEARCH,READ',authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<Priority>> getPriorities(
            @ApiParam(value = "id of account", required = false, defaultValue = "") @PathVariable(name = "accountId", required = false) BigInteger accountId,
            @ApiParam(value = "Priority Type", defaultValue = "", required = false) @RequestParam(value = "priorityType", defaultValue = "INCIDENT", required = false) String priorityType,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Priority>>(priorityService.getPriorities(accountId, priorityType, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all holiday calenders", nickname = "getHolidayCalenders", notes = "Get holiday calenders", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-holiday-callender", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<HolidayCalender>> getHolidayCalenders(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<HolidayCalender>>(holidayCalenderService.getHolidayCalenders(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find holiday calender id", nickname = "getHolidayCalenderById", notes = "Find holiday calender id", response = Location.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Holiday calender not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/holiday-callender/{holidayCallenderId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','READ',authentication.principal)")
    public ResponseEntity<HolidayCalender> getHolidayCalenderById(@ApiParam(value = "id of holiday-callender", required = true) @PathVariable("holidayCallenderId") BigInteger holidayCallenderId) {
        return new ResponseEntity<HolidayCalender>(holidayCalenderService.getById(holidayCallenderId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload holiday calender data csv", nickname = "uploadHolidayCalenderData", notes = "Upload holiday calender data", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/upload-holiday-calender", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','WRITE',authentication.principal)")
    public ResponseEntity<HolidayCalender> uploadHolidayCalenderData(
            @ApiParam(value = "Holiday Calender CSV file", required = true) @RequestPart(name = "file", required = true) MultipartFile multipartFile,
            @ApiParam(value = "Holiday Calender object", required = true) @RequestPart("holidayCalender") HolidayCalender holidayCalender
    ) throws IOException {
        return new ResponseEntity<HolidayCalender>(holidayCalenderService.uploadHolidayCalenderData(multipartFile, holidayCalender), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Downoad holiday celender", nickname = "downloadHolidayCalender", notes = "Download holiday celender", response = Resource.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = Resource.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/holiday-callender/{holidayCallenderId}/download", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','READ',authentication.principal)")
    public ResponseEntity<Resource> downloadHolidayCalender(
            @ApiParam(value = "id of holiday-callender", required = true) @PathVariable("holidayCallenderId") BigInteger holidayCallenderId
    ) {
        final InputStreamResource resource = new InputStreamResource(holidayCalenderService.downloadHolidayCalender(holidayCallenderId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "holiday-calender-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Notifications", nickname = "getHolidayCalenders", notes = "Get all Notifications", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-notifications", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<Notification>> getNotifications(
            @ApiParam(value = "Search Value", defaultValue = "ALL", required = false) @RequestParam(value = "searchValue", defaultValue = "", required = false) String searchValue,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Notification>>(notificationService.getNotifications(searchValue, Status.valueOf(status), pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find notification id", nickname = "getNotificationById", notes = "Find notification id", response = Notification.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Notification not found")
    })
    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/notification/{notificationId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Notification> getNotificationById(@ApiParam(value = "id of notification", required = true) @PathVariable("notificationId") BigInteger notificationId) {
        return new ResponseEntity<Notification>(notificationService.getById(notificationId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update notification status", nickname = "updateNotificationStatus", notes = "Update notification status", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/notification/{ids}/{status}", method = RequestMethod.GET)
    public ResponseEntity updateNotificationStatus(
            @NotNull @ApiParam(value = "Notification id's to update", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @ApiParam(value = "Status", required = true) @PathVariable("status") Status status
    ) {
        notificationService.updateNotificationStatus(ids, status);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save notification", nickname = "saveNotification", notes = "Save notification", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/notification", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity saveNotification(
            @ApiParam(value = "Notification list object", required = true) @Valid @RequestBody List<Notification> body
    ) {
        notificationService.save(body);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find mapdl by id", nickname = "getDistributionListById", notes = "Find mapdl by id", response = Department.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "MapDl not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/distribution-list/{dlid}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','READ',authentication.principal)")
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<DistributionList> getDistributionListById(@ApiParam(value = "id of mapdl", required = true) @PathVariable("dlid") BigInteger dlid) {
        return new ResponseEntity<DistributionList>(distributionListService.getById(dlid), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all map dl", nickname = "getDistributionLists", notes = "Get all map dl", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-distribution-list", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','READ',authentication.principal)")
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<DistributionList>> getDistributionLists(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<DistributionList>>(distributionListService.getDistributionLists(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save a map dl", nickname = "saveMapDL", notes = "Save a map dl", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/distribution-list", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<DistributionList> saveMapDL(@ApiParam(value = "Mapdl object", required = true) @Valid @RequestBody DistributionList body) {
        return new ResponseEntity<DistributionList>(distributionListService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find vendor by id", nickname = "getVendorById", notes = "Find vendor by id", response = Department.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "MapDl not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/vendor/{vendorId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR','READ',authentication.principal)")
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<Vendor> getVendorById(@ApiParam(value = "id of vendor", required = true) @PathVariable("vendorId") BigInteger vendorId) {
        return new ResponseEntity<Vendor>(vendorService.getById(vendorId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all vendor", nickname = "getVendors", notes = "Get all vendor", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-vendor", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR,ORDER ASSET','READ,WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<PaginatedList<Vendor>> getVendors(
            @ApiParam(value = "Vendor Type", defaultValue = "", required = false) @RequestParam(value = "vendorType", defaultValue = "INCIDENT", required = false) String vendorType,
            @ApiParam(value = "In House Vendor", defaultValue = "", required = false) @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Vendor>>(vendorService.getVendors(inHouse, VendorType.valueOf(vendorType), pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save a vendor", nickname = "saveVendor", notes = "Save a vendor", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/vendor", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR','WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<Vendor> saveVendor(@ApiParam(value = "Vendor object", required = true) @Valid @RequestBody Vendor body) {
        return new ResponseEntity<Vendor>(vendorService.save(body), HttpStatus.OK);
    }

    /*@RequestMapping(value = "/notification/dummy", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity generateDummyNotification(
            @ApiParam(value = "NotificationVO list object", required = true) @Valid @RequestBody NotificationVO body
    ) throws JsonProcessingException {
        log.info("Consumed message: " + objectMapper.writeValueAsString(body));
        simpMessagingTemplate.convertAndSend("/topic/notification/" + body.getUserId(), objectMapper.writeValueAsString(body));
        return new ResponseEntity(body, HttpStatus.OK);
    }*/

    @RequestMapping(value = "/chat-room", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<ChatRoom> save(@ApiParam(value = "Chat Room object", required = true) @Valid @RequestBody ChatRoom body) {
        return new ResponseEntity<ChatRoom>(chatRoomService.save(body), HttpStatus.OK);
    }

    @RequestMapping(value = "/all-chat-room", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<ChatRoom>> findAll(
            @ApiParam(value = "Chat Room Id", defaultValue = "", required = false) @RequestParam(value = "chatRoomNo", defaultValue = "", required = false) String chatRoomNo,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<ChatRoom>>(chatRoomService.findAll(chatRoomNo, pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message/{chatRoomId}", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<List<ChatMessage>> getChatMessages(@ApiParam(value = "id of chat room", required = true) @PathVariable("chatRoomId") String chatRoomId) {
        return new ResponseEntity<List<ChatMessage>>(chatMessageService.chatMassages(chatRoomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<ChatMessage> save(@ApiParam(value = "Chat Message object", required = true) @Valid @RequestBody ChatMessage body) {
        return new ResponseEntity<ChatMessage>(chatMessageService.save(body), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message/action/{chatRoomId}", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    public ResponseEntity<List<ChatMessage>> chatAction(@ApiParam(value = "id of chat room", required = true) @PathVariable("chatRoomId") String chatRoomId) {
        return new ResponseEntity<List<ChatMessage>>(chatMessageService.chatAction(chatRoomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message/close/{chatRoomId}", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    public ResponseEntity<List<ChatMessage>> chatClose(@ApiParam(value = "id of chat room", required = true) @PathVariable("chatRoomId") String chatRoomId) {
        return new ResponseEntity<List<ChatMessage>>(chatMessageService.chatClose(chatRoomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat/dummy", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity generateDummyNotification(
            @RequestBody ChatMessage body
    ) throws JsonProcessingException {
        log.info("Consumed message: " + objectMapper.writeValueAsString(body));
        simpMessagingTemplate.convertAndSend("/topic/chat/1cf7936e-2984-4581-b4c6-bb781353b20a/12", objectMapper.writeValueAsString(body));
        return new ResponseEntity(body, HttpStatus.OK);
    }

    /*@RequestMapping(value = "/chat-message", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<ChatMessage> save(@ApiParam(value = "Chat Message object", required = true) @Valid @RequestBody ChatMessage body) {
        return new ResponseEntity<ChatMessage>(chatMessageService.save(body), HttpStatus.OK);
    }*/

    /*@RequestMapping(value = "/all-chat-message", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<ChatMessage>> findAll(
            @ApiParam(value = "Chat Room Id", defaultValue = "", required = true) @RequestParam(value = "chatRoomId", defaultValue = "", required = false) BigInteger chatRoomId,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<ChatMessage>>(chatMessageService.findAll(chatRoomId, pageable), HttpStatus.OK);
    }*/

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save a account", nickname = "saveAccount", notes = "Save a account", tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/account", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ACCOUNT','READ|WRITE',authentication.principal)")
    public ResponseEntity<Account> saveAccount(@ApiParam(value = "Account object", required = true) @Valid @RequestBody Account body) {
        return new ResponseEntity<Account>(accountService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find account by id", nickname = "getAccountById", notes = "Find account by id", response = Account.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Location not found"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/account/{accountId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ACCOUNT','READ',authentication.principal)")
    public ResponseEntity<Account> getAccountById(@ApiParam(value = "id of account", required = true) @PathVariable("accountId") BigInteger accountId) {
        return new ResponseEntity<Account>(accountService.getById(accountId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Accounts", nickname = "getAccounts", notes = "Get all Accounts", response = PaginatedList.class, tags = {"Misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/all-account", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ACCOUNT,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Account>> getAccounts(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Account>>(accountService.getAccounts(pageable), HttpStatus.OK);
    }

}