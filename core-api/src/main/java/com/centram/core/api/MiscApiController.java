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


    @RequestMapping(value = "/request-demo", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> requestADemo(@Valid @RequestBody RequestDemoDTO body) {
        return new ResponseEntity<CommonResponse>(miscService.requestDemo(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/module/{moduleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Module> getModuleById(@PathVariable("moduleId") BigInteger moduleId) {
        return new ResponseEntity<Module>(moduleService.getModuleById(moduleId), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(value = "/all-module", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Module>> getModules(
            @NotNull @Valid @RequestParam(value = "licenseType", defaultValue = "ALL", required = false) String licenseType,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Module>>(moduleService.getModules(licenseType, pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/all-asset-model", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<AssetModel>> getAssetModels(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<List<AssetModel>>(assetModelService.getAssetModels(pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/role/{roleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Role> getRoleById(@PathVariable("roleId") BigInteger roleId) {
        return new ResponseEntity<Role>(roleService.getById(roleId), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(value = "/all-role", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Role>> getRoles(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Role>>(roleService.getRoles(pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/permission", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PERMISSION','WRITE',authentication.principal)")
    public ResponseEntity<Void> savePermission(@Valid @RequestBody PermissionDTO body) {
        permissionService.save(body);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/action/{actionId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Action> getActionById(@PathVariable("actionId") BigInteger actionId) {
        return new ResponseEntity<Action>(actionService.getById(actionId), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(value = "/all-action", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Action>> getActions(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Action>>(actionService.getActions(pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-action-by-role-module/{roleId}/{moduleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<Action>> getActionsByRoleAndModule(
            @PathVariable("roleId") BigInteger roleId,
            @PathVariable("moduleId") BigInteger moduleId
    ) {
        return new ResponseEntity<List<Action>>(permissionService.getActionsByRoleAndModule(roleId, moduleId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-module-by-role/{roleId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<Module>> getModulesByRole(@PathVariable("roleId") BigInteger roleId) {
        return new ResponseEntity<List<Module>>(permissionService.getModulesByRole(roleId), HttpStatus.OK);
    }


    @RequestMapping(value = "/department/{departmentId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','READ',authentication.principal)")
    public ResponseEntity<Department> getDepartentById(@PathVariable("departmentId") BigInteger departmentId) {
        return new ResponseEntity<Department>(departmentService.getById(departmentId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-department", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Department>> getDepartments(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Department>>(departmentService.getDepartments(pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/department", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','WRITE',authentication.principal)")
    public ResponseEntity<Department> saveDepartment(@Valid @RequestBody Department body) {
        return new ResponseEntity<Department>(departmentService.save(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/department/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DEPARTMENT','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateDepartmentsStatus(
            @NotNull @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @PathVariable("status") Status status
    ) {
        departmentService.updateDepartmentsStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @RequestMapping(value = "/location", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','READ',authentication.principal)")
    public ResponseEntity<Location> saveLocation(@Valid @RequestBody Location body) {
        return new ResponseEntity<Location>(locationService.save(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/location/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateLocationsStatus(@NotNull @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @PathVariable("status") Status status) {
        locationService.updateLocationsStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @RequestMapping(value = "/location/{locationId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION','READ',authentication.principal)")
    public ResponseEntity<Location> getLocationById(@PathVariable("locationId") BigInteger locationId) {
        return new ResponseEntity<Location>(locationService.getById(locationId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-location", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('LOCATION,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Location>> getLocations(
            @RequestParam(name = "accountId", required = false) BigInteger accountId,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Location>>(locationService.getLocations(accountId, pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/priority", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','WRITE',authentication.principal)")
    public ResponseEntity<Priority> savePriority(@Valid @RequestBody Priority body) {
        return new ResponseEntity<Priority>(priorityService.save(body), HttpStatus.OK);
    }


    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','WRITE',authentication.principal)")
    @RequestMapping(value = "/priority/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    public ResponseEntity<Void> updatePrioritiesStatus(@NotNull @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @PathVariable("status") Status status) {
        priorityService.updatePrioritiesStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @RequestMapping(value = "/priority/{priorityId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY','READ',authentication.principal)")
    public ResponseEntity<Priority> getPriorityById(@PathVariable("priorityId") BigInteger priorityId) {
        return new ResponseEntity<Priority>(priorityService.getById(priorityId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-priority", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('PRIORITY,MY INCIDENTS,MY GROUP INCIDENTS,REPORT,','READ,WRITE|SEARCH,WRITE|SEARCH,READ',authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<Priority>> getPriorities(
            @PathVariable(name = "accountId", required = false) BigInteger accountId,
            @RequestParam(value = "priorityType", defaultValue = "INCIDENT", required = false) String priorityType,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Priority>>(priorityService.getPriorities(accountId, priorityType, pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-holiday-callender", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','READ',authentication.principal)")
    public ResponseEntity<PaginatedList<HolidayCalender>> getHolidayCalenders(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<HolidayCalender>>(holidayCalenderService.getHolidayCalenders(pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/holiday-callender/{holidayCallenderId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','READ',authentication.principal)")
    public ResponseEntity<HolidayCalender> getHolidayCalenderById(@PathVariable("holidayCallenderId") BigInteger holidayCallenderId) {
        return new ResponseEntity<HolidayCalender>(holidayCalenderService.getById(holidayCallenderId), HttpStatus.OK);
    }


    @RequestMapping(value = "/upload-holiday-calender", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','WRITE',authentication.principal)")
    public ResponseEntity<HolidayCalender> uploadHolidayCalenderData(
            @RequestPart(name = "file", required = true) MultipartFile multipartFile,
            @RequestPart("holidayCalender") HolidayCalender holidayCalender
    ) throws IOException {
        return new ResponseEntity<HolidayCalender>(holidayCalenderService.uploadHolidayCalenderData(multipartFile, holidayCalender), HttpStatus.OK);
    }


    @RequestMapping(value = "/holiday-callender/{holidayCallenderId}/download", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('HOLIDAY CALENDAR','READ',authentication.principal)")
    public ResponseEntity<Resource> downloadHolidayCalender(
            @PathVariable("holidayCallenderId") BigInteger holidayCallenderId
    ) {
        final InputStreamResource resource = new InputStreamResource(holidayCalenderService.downloadHolidayCalender(holidayCallenderId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "holiday-calender-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }


    @RequestMapping(value = "/all-notifications", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<Notification>> getNotifications(
            @RequestParam(value = "searchValue", defaultValue = "", required = false) String searchValue,
            @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Notification>>(notificationService.getNotifications(searchValue, Status.valueOf(status), pageable), HttpStatus.OK);
    }


    @JsonView(Views.BasicView.class)
    @RequestMapping(value = "/notification/{notificationId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Notification> getNotificationById(@PathVariable("notificationId") BigInteger notificationId) {
        return new ResponseEntity<Notification>(notificationService.getById(notificationId), HttpStatus.OK);
    }


    @RequestMapping(value = "/notification/{ids}/{status}", method = RequestMethod.GET)
    public ResponseEntity updateNotificationStatus(
            @NotNull @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids,
            @PathVariable("status") Status status
    ) {
        notificationService.updateNotificationStatus(ids, status);
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "/notification", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity saveNotification(
            @Valid @RequestBody List<Notification> body
    ) {
        notificationService.save(body);
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "/distribution-list/{dlid}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','READ',authentication.principal)")
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<DistributionList> getDistributionListById(@PathVariable("dlid") BigInteger dlid) {
        return new ResponseEntity<DistributionList>(distributionListService.getById(dlid), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-distribution-list", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','READ',authentication.principal)")
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<DistributionList>> getDistributionLists(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<DistributionList>>(distributionListService.getDistributionLists(pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/distribution-list", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('DISTRIBUTION LIST','WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<DistributionList> saveMapDL(@Valid @RequestBody DistributionList body) {
        return new ResponseEntity<DistributionList>(distributionListService.save(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/vendor/{vendorId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR','READ',authentication.principal)")
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<Vendor> getVendorById(@PathVariable("vendorId") BigInteger vendorId) {
        return new ResponseEntity<Vendor>(vendorService.getById(vendorId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-vendor", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR,ORDER ASSET','READ,WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<PaginatedList<Vendor>> getVendors(
            @RequestParam(value = "vendorType", defaultValue = "INCIDENT", required = false) String vendorType,
            @RequestParam(value = "inHouse", defaultValue = "", required = false) String inHouse,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Vendor>>(vendorService.getVendors(inHouse, VendorType.valueOf(vendorType), pageable), HttpStatus.OK);
    }


    @RequestMapping(value = "/vendor", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('VENDOR','WRITE',authentication.principal)")
    @JsonView(Views.DetailView.class)
    public ResponseEntity<Vendor> saveVendor(@Valid @RequestBody Vendor body) {
        return new ResponseEntity<Vendor>(vendorService.save(body), HttpStatus.OK);
    }

    /*@RequestMapping(value = "/notification/dummy", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity generateDummyNotification(
             @Valid @RequestBody NotificationVO body
    ) throws JsonProcessingException {
        log.info("Consumed message: " + objectMapper.writeValueAsString(body));
        simpMessagingTemplate.convertAndSend("/topic/notification/" + body.getUserId(), objectMapper.writeValueAsString(body));
        return new ResponseEntity(body, HttpStatus.OK);
    }*/

    @RequestMapping(value = "/chat-room", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<ChatRoom> save(@Valid @RequestBody ChatRoom body) {
        return new ResponseEntity<ChatRoom>(chatRoomService.save(body), HttpStatus.OK);
    }

    @RequestMapping(value = "/all-chat-room", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<ChatRoom>> findAll(
            @RequestParam(value = "chatRoomNo", defaultValue = "", required = false) String chatRoomNo,
            @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<ChatRoom>>(chatRoomService.findAll(chatRoomNo, pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message/{chatRoomId}", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView({Views.DetailView.class,})
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable("chatRoomId") String chatRoomId) {
        return new ResponseEntity<List<ChatMessage>>(chatMessageService.chatMassages(chatRoomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<ChatMessage> save(@Valid @RequestBody ChatMessage body) {
        return new ResponseEntity<ChatMessage>(chatMessageService.save(body), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message/action/{chatRoomId}", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    public ResponseEntity<List<ChatMessage>> chatAction(@PathVariable("chatRoomId") String chatRoomId) {
        return new ResponseEntity<List<ChatMessage>>(chatMessageService.chatAction(chatRoomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/chat-message/close/{chatRoomId}", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    public ResponseEntity<List<ChatMessage>> chatClose(@PathVariable("chatRoomId") String chatRoomId) {
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
    public ResponseEntity<ChatMessage> save( @Valid @RequestBody ChatMessage body) {
        return new ResponseEntity<ChatMessage>(chatMessageService.save(body), HttpStatus.OK);
    }*/

    /*@RequestMapping(value = "/all-chat-message", produces = {"application/json"}, method = RequestMethod.GET)
    @JsonView(Views.ListView.class)
    public ResponseEntity<PaginatedList<ChatMessage>> findAll(
             @RequestParam(value = "chatRoomId", defaultValue = "", required = false) BigInteger chatRoomId,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<ChatMessage>>(chatMessageService.findAll(chatRoomId, pageable), HttpStatus.OK);
    }*/


    @RequestMapping(value = "/account", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ACCOUNT','READ|WRITE',authentication.principal)")
    public ResponseEntity<Account> saveAccount(@Valid @RequestBody Account body) {
        return new ResponseEntity<Account>(accountService.save(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/account/{accountId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ACCOUNT','READ',authentication.principal)")
    public ResponseEntity<Account> getAccountById(@PathVariable("accountId") BigInteger accountId) {
        return new ResponseEntity<Account>(accountService.getById(accountId), HttpStatus.OK);
    }


    @RequestMapping(value = "/all-account", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ACCOUNT,ORGANISATION,USER,ORDER ASSET','READ,WRITE,WRITE,WRITE',authentication.principal)")
    public ResponseEntity<PaginatedList<Account>> getAccounts(@PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Account>>(accountService.getAccounts(pageable), HttpStatus.OK);
    }

}