package com.centram.core.api;


import com.centram.common.dto.AuthRequestDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.UserDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.JwtTokenUtil;
import com.centram.common.utility.PaginatedList;
import com.centram.common.utility.Utility;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.LoggedInUserVO;
import com.centram.common.vo.UserVO;
import com.centram.core.service.UserService;
import com.centram.domain.User;
import com.centram.domain.enumarator.Status;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;


@RequestMapping(value = "/api/v1/user")
@Controller
public class UserApiController {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * user signin
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/sign-in", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<LoggedInUserVO> login(@Valid @RequestBody AuthRequestDTO body) {
        try {
            LoggedInUser loggedInUser = (LoggedInUser) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.getUsername(), Utility.decode(body.getPassword()))).getPrincipal();
            loggedInUser.setAuthToken(jwtTokenUtil.generateToken(loggedInUser, body.getRememberMe()));
            LoggedInUserVO loggedInUserVO = new LoggedInUserVO(loggedInUser);
            return ResponseEntity.ok().headers(new HttpHeaders() {{
                set("Authorization", loggedInUser.getAuthToken());
            }}).body(loggedInUserVO);
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    /**
     * user SSO signin
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/sso-sign-in", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<LoggedInUserVO> SSOlogin(@Valid @RequestBody AuthRequestDTO body) {
        try {
            String email = body.getUsername();
            log.info("SSO principal => {} ", email);
            //LoggedInUser loggedInUser = userService.getUserByPrincipal("centramsuperadm@gmail.com");
            LoggedInUser loggedInUser = userService.getUserByPrincipal(email);
            loggedInUser.setAuthToken(jwtTokenUtil.generateToken(loggedInUser, true));
            LoggedInUserVO loggedInUserVO = new LoggedInUserVO(loggedInUser);
            return ResponseEntity.ok().headers(new HttpHeaders() {{
                set("Authorization", loggedInUser.getAuthToken());
            }}).body(loggedInUserVO);
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    /**
     * user signout
     *
     * @return
     */
    @RequestMapping(value = "/sign-out", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity logout() {
        userService.signOut();
        return ResponseEntity.ok(null);
    }

    /**
     * forgot password
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/forgot-password", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> forgotPassword(@Valid @RequestBody AuthRequestDTO body) {
        return new ResponseEntity<CommonResponse>(userService.forgotPassword(body), HttpStatus.OK);
    }

    /**
     * reset password
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/reset-password", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> resetPassword(@Valid @RequestBody AuthRequestDTO body) {
        return new ResponseEntity<CommonResponse>(userService.resetPassword(body), HttpStatus.OK);
    }

    /**
     * change password
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/change-password", consumes = {"application/json"}, method = RequestMethod.PUT)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody UserDTO body) {
        userService.changePassword(body);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * get all users based on logedin and search
     *
     * @param email
     * @param employeeId
     * @param status
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ASSET ASSIGNMENT REPORT,USER,MY INCIDENTS,MY GROUP INCIDENTS,ORDER ASSET,ALLOCATE PROJECT,DEALLOCATE PROJECT','READ,READ,WRITE|SEARCH,WRITE|SEARCH,READ|WRITE,ASSIGN,DEALLOCATE',authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<UserVO>> getUsers(@RequestParam(value = "email", defaultValue = "", required = false) String email, @RequestParam(value = "employeeId", defaultValue = "", required = false) String employeeId, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @RequestParam(value = "filterType", defaultValue = "", required = false) String filterType, @RequestParam(value = "vendorId", defaultValue = "", required = false) BigInteger vendorId, @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<UserVO>>(userService.getUsers(email, employeeId, Status.valueOf(status), filterType, vendorId, pageable), HttpStatus.OK);
    }

    /**
     * save user
     *
     * @param body
     * @return
     */
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER','WRITE',authentication.principal)")
    public ResponseEntity<UserVO> save(@Valid @RequestBody User body) {
        return new ResponseEntity<UserVO>(userService.save(body), HttpStatus.OK);
    }

    /**
     * update user status
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping(value = "/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER','WRITE',authentication.principal)")
    public ResponseEntity<Void> updateStatus(@NotNull @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @PathVariable("status") Status status) {
        userService.updateUsersStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * get user by id
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{userId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER,MY INCIDENTS,MY GROUP INCIDENTS','READ,WRITE,WRITE',authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<UserVO> getUserById(@PathVariable("userId") BigInteger userId) {
        return new ResponseEntity<UserVO>(userService.getUserById(userId), HttpStatus.OK);
    }

    /**
     * download users in csv
     *
     * @return
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER','READ',authentication.principal)")
    public ResponseEntity<Resource> downloadUsers() {
        final InputStreamResource resource = new InputStreamResource(userService.downloadUsers());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "users-" + System.currentTimeMillis() + ".csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }

    /**
     * upload users in csv
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER','WRITE',authentication.principal)")
    public ResponseEntity uploadUsersData(@RequestParam(name = "file", required = true) MultipartFile multipartFile) throws IOException {
        userService.uploadUsersData(multipartFile);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * find users by module permission
     *
     * @param moduleIds
     * @param actionName
     * @return
     */
    @RequestMapping(value = "/find-by-modules-permissions", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<List<UserVO>> getUsersByModuleAndAction(@NotNull @Valid @RequestParam(value = "moduleIds", required = true) List<BigInteger> moduleIds, @NotNull @Valid @RequestParam(value = "actionName", required = true) String actionName) {
        return new ResponseEntity<List<UserVO>>(userService.getUsersByModuleAndAction(moduleIds, actionName), HttpStatus.OK);
    }

    /*
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by multi Id", nickname = "getUserByIds", notes = "Find user by multi Id", response = UserVO.class, responseContainer = "List", tags = {"User",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List")
    })
    @RequestMapping(value = "/findByIds", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER','READ',authentication.principal)")
    public ResponseEntity<Page<UserVO>> getUserByIds(@NotNull  @Valid @RequestParam(value = "ids", required = true) List<BigInteger> ids,  @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<UserVO>>(userService.getUserByIds(ids, pageable), HttpStatus.OK);
    }
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by username", nickname = "getUserByUserName", notes = "Find user by username", response = UserVO.class, tags = {"User",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid username supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/{userName}/findByUserName", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('USER','READ',authentication.principal)")
    public ResponseEntity<UserVO> getUserByUserName( @PathVariable("userName") String userName) {
        return new ResponseEntity<UserVO>(userService.getUserByUserName(userName), HttpStatus.OK);
    }
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get user settings", nickname = "getUserSettings", notes = "Get user settings", response = UserDTO.class, tags = {"User",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/get-settings", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUserSettings() {
        return new ResponseEntity<UserDTO>(userService.getUserSettings(), HttpStatus.OK);
    }
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload user profile photo", nickname = "uploadUserProfile", notes = "Upload user profile photo", tags = {"User",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/upload-profile-photo", produces = {"application/json"}, method = RequestMethod.POST)
    //@PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<UserDTO> uploadUserProfile(HttpServletRequest request) {
        return new ResponseEntity<UserDTO>(userService.uploadUserProfile(request), HttpStatus.OK);
    }
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get User Activity", nickname = "getActivityLogs", notes = "Get User Activity", response = List.class, tags = {"User",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/activity-log", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Page<ActivityLog>> getActivityLogs( @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<ActivityLog>>(userService.getActivityLogs(pageable), HttpStatus.OK);
    }*/

}