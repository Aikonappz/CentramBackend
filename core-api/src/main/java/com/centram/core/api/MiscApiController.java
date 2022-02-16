package com.centram.core.api;


import com.centram.common.dto.RequestDemoDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.common.vo.CommonResponse;
import com.centram.core.service.*;
import com.centram.domain.Module;
import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
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
@Api(value = "misc", description = "Misc API")
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
    private DepartmentService departmentService;

    @Autowired
    private PriorityService priorityService;

    @Autowired
    private HolidayCalenderService holidayCalenderService;

    @Autowired
    private MiscService miscService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private VendorService vendorService;

    @ApiOperation(value = "Demo Request Api", nickname = "requestDemo", notes = "Demo Request Api", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/request-demo", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> requestDemo(@ApiParam(value = "AuthRequest object", required = true) @Valid @RequestBody RequestDemoDTO body) {
        return new ResponseEntity<CommonResponse>(miscService.requestDemo(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find module by id", nickname = "getModuleById", notes = "Find module by id", response = Module.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Role.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Module not found")
    })
    @RequestMapping(value = "/module/{moduleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Module> getModuleById(@ApiParam(value = "id of module", required = true) @PathVariable("moduleId") BigInteger moduleId) {
        return new ResponseEntity<Module>(moduleService.getModuleById(moduleId), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Modules", nickname = "getModules", notes = "Get all Modules", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-module", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Module>> getModules(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Module>>(moduleService.getModules(pageable), HttpStatus.OK);
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Roles", nickname = "getRoles", notes = "Get all Roles", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-role", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Role>> getRoles(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Role>>(roleService.getRoles(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find department by id", nickname = "getDepartentById", notes = "Find department by id", response = Department.class, tags = {"misc",})
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all departments", nickname = "getDepartments", notes = "Get all departments", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-department", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Department>> getDepartments(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Department>>(departmentService.getDepartments(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a department", nickname = "saveDepartment", notes = "Add a department", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/department", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','WRITE',authentication.principal)")
    public ResponseEntity<Department> saveDepartment(@ApiParam(value = "Department object", required = true) @Valid @RequestBody Department body) {
        return new ResponseEntity<Department>(departmentService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of department's", nickname = "updateStatus", notes = "Update status of department's", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Department not found")
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a location", nickname = "saveLocation", notes = "Add a location", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/location", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','READ',authentication.principal)")
    public ResponseEntity<Location> saveLocation(@ApiParam(value = "Location object", required = true) @Valid @RequestBody Location body) {
        return new ResponseEntity<Location>(locationService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of location's", nickname = "updateStatus", notes = "Update status of location's", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Location not found")
    })
    @RequestMapping(value = "/location/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateLocationsStatus(@NotNull @ApiParam(value = "Location id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Status", required = true) @PathVariable("status") Status status) {
        locationService.updateLocationsStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find location by id", nickname = "getLocationById", notes = "Find location by id", response = Location.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Location not found")
    })
    @RequestMapping(value = "/location/{locationId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','READ',authentication.principal)")
    public ResponseEntity<Location> getLocationById(@ApiParam(value = "id of location", required = true) @PathVariable("locationId") BigInteger locationId) {
        return new ResponseEntity<Location>(locationService.getById(locationId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Locations", nickname = "getLocations", notes = "Get all Locations", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-location", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Location>> getLocations(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Location>>(locationService.getLocations(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a Priority", nickname = "savePriority", notes = "Add a Priority", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/priority", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','WRITE',authentication.principal)")
    public ResponseEntity<Priority> savePriority(@ApiParam(value = "Priority object", required = true) @Valid @RequestBody Priority body) {
        return new ResponseEntity<Priority>(priorityService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of priorityies", nickname = "updatePrioritiesStatus", notes = "Update status of priorityies", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "priority not found")
    })
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','WRITE',authentication.principal)")
    @RequestMapping(value = "/priority/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    public ResponseEntity<Void> updatePrioritiesStatus(@NotNull @ApiParam(value = "Priority id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Status", required = true) @PathVariable("status") Status status) {
        priorityService.updatePrioritiesStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find priority by id", nickname = "getPriorityById", notes = "Find priority by id", response = Location.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "Location not found")
    })
    @RequestMapping(value = "/priority/{priorityId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','READ',authentication.principal)")
    public ResponseEntity<Priority> getPriorityById(@ApiParam(value = "id of priority", required = true) @PathVariable("priorityId") BigInteger priorityId) {
        return new ResponseEntity<Priority>(priorityService.getById(priorityId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all priorities", nickname = "getPriorities", notes = "Get all priorities", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-priority", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY,MY INCIDENTS,MY GROUP INCIDENTS,REPORT,','READ,WRITE|SEARCH,WRITE|SEARCH,READ',authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<Priority>> getPriorities(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Priority>>(priorityService.getPriorities(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all holiday calenders", nickname = "getHolidayCalenders", notes = "Get holiday calenders", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-holiday-callender", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDER','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<HolidayCalender>> getHolidayCalenders(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<HolidayCalender>>(holidayCalenderService.getHolidayCalenders(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find holiday calender id", nickname = "getHolidayCalenderById", notes = "Find holiday calender id", response = Location.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "holiday calender not found")
    })
    @RequestMapping(value = "/holiday-callender/{holidayCallenderId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDER','READ',authentication.principal)")
    public ResponseEntity<HolidayCalender> getHolidayCalenderById(@ApiParam(value = "id of holiday-callender", required = true) @PathVariable("holidayCallenderId") BigInteger holidayCallenderId) {
        return new ResponseEntity<HolidayCalender>(holidayCalenderService.getById(holidayCallenderId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload holiday calender data csv", nickname = "uploadHolidayCalenderData", notes = "Upload holiday calender data", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/upload-holiday-calender", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDER','WRITE',authentication.principal)")
    public ResponseEntity<HolidayCalender> uploadHolidayCalenderData(
            @ApiParam(value = "Holiday Calender CSV file", required = true) @RequestPart(name = "file", required = true) MultipartFile multipartFile,
            @ApiParam(value = "Holiday Calender object", required = true) @RequestPart("holidayCalender") HolidayCalender holidayCalender
    ) throws IOException {
        return new ResponseEntity<HolidayCalender>(holidayCalenderService.uploadHolidayCalenderData(multipartFile, holidayCalender), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Downoad holiday celender", nickname = "downloadHolidayCalender", notes = "Download holiday celender", response = Resource.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Resource.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/holiday-callender/{holidayCallenderId}/download", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDER','READ',authentication.principal)")
    public ResponseEntity<Resource> downloadHolidayCalender(
            @ApiParam(value = "id of holiday-callender", required = true) @PathVariable("holidayCallenderId") BigInteger holidayCallenderId
    ) {
        final InputStreamResource resource = new InputStreamResource(holidayCalenderService.downloadHolidayCalender(holidayCallenderId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "holiday-calender-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Notifications", nickname = "getHolidayCalenders", notes = "Get all Notifications", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find notification id", nickname = "getNotificationById", notes = "Find notification id", response = Notification.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Location.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "holiday calender not found")
    })
    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/notification/{notificationId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Notification> getNotificationById(@ApiParam(value = "id of notification", required = true) @PathVariable("notificationId") BigInteger notificationId) {
        return new ResponseEntity<Notification>(notificationService.getById(notificationId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update notification status", nickname = "updateNotificationStatus", notes = "Update notification status", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/notification/{ids}/{status}", method = RequestMethod.GET)
    public ResponseEntity updateNotificationStatus(
            @NotNull @ApiParam(value = "Notification id's to update", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @ApiParam(value = "Status", required = true) @PathVariable("status") Status status
    ) {
        notificationService.updateNotificationStatus(ids, status);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add notification", nickname = "saveNotification", notes = "Add notification", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/notification", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity saveNotification(
            @ApiParam(value = "Notification list object", required = true) @Valid @RequestBody List<Notification> body
    ) {
        notificationService.save(body);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find mapdl by id", nickname = "getDistributionListById", notes = "Find mapdl by id", response = Department.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "MapDl not found")
    })
    @RequestMapping(value = "/distribution-list/{dlid}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','READ',authentication.principal)")
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<DistributionList> getDistributionListById(@ApiParam(value = "id of mapdl", required = true) @PathVariable("dlid") BigInteger dlid) {
        return new ResponseEntity<DistributionList>(distributionListService.getById(dlid), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all map dl", nickname = "getDistributionLists", notes = "Get all map dl", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-distribution-list", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','READ',authentication.principal)")
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<DistributionList>> getDistributionLists(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<DistributionList>>(distributionListService.getDistributionLists(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a map dl", nickname = "saveMapDL", notes = "Add a map dl", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/distribution-list", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<DistributionList> saveMapDL(@ApiParam(value = "Mapdl object", required = true) @Valid @RequestBody DistributionList body) {
        return new ResponseEntity<DistributionList>(distributionListService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find vendor by id", nickname = "getVendorById", notes = "Find vendor by id", response = Department.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Department.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "MapDl not found")
    })
    @RequestMapping(value = "/vendor/{vendorId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR','READ',authentication.principal)")
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<Vendor> getVendorById(@ApiParam(value = "id of vendor", required = true) @PathVariable("vendorId") BigInteger vendorId) {
        return new ResponseEntity<Vendor>(vendorService.getById(vendorId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all vendor", nickname = "getVendors", notes = "Get all vendor", response = PaginatedList.class, tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all-vendor", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR,ORDER ASSET','READ,WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<PaginatedList<Vendor>> getVendors(
            @ApiParam(value = "In House Vendor", defaultValue = "", required = false) @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Vendor>>(vendorService.getVendors(inHouse, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a vendor", nickname = "saveVendor", notes = "Add a vendor", tags = {"misc",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
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
}