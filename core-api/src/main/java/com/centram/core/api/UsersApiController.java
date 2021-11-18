package com.centram.core.api;


import com.centram.common.dto.AuthRequestDTO;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.UserDTO;
import com.centram.common.utility.JwtTokenUtil;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.CommonResponse;
import com.centram.common.vo.LoggedInUserVO;
import com.centram.common.vo.UserVO;
import com.centram.core.service.UserService;
import com.centram.domain.ActivityLog;
import com.centram.domain.User;
import com.centram.domain.enumarator.Status;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "users", description = "User Api")
@RequestMapping(value = "/api/v1/user")
@Controller
public class UsersApiController {

    private static final Logger log = LoggerFactory.getLogger(UsersApiController.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @ApiOperation(value = "SignIn Api", nickname = "SignIn", notes = "SignIn user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/sign-in", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<LoggedInUserVO> login(@ApiParam(value = "AuthRequest object", required = true) @Valid @RequestBody AuthRequestDTO body) {
        try {
            LoggedInUser loggedInUser = (LoggedInUser) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())).getPrincipal();
            loggedInUser.setAuthToken(jwtTokenUtil.generateToken(loggedInUser, body.getRememberMe()));
            LoggedInUserVO loggedInUserVO = new LoggedInUserVO(userService.getProfilePhoto(loggedInUser.getUserId()), loggedInUser);
            HttpHeaders responseHeaders = new HttpHeaders() {{
                set("Authorization", loggedInUser.getAuthToken());
            }};
            return ResponseEntity.ok().headers(responseHeaders).body(loggedInUserVO);
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Logout", nickname = "logout", notes = "logout", response = CommonResponse.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
    })
    @RequestMapping(value = "/sign-out", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<CommonResponse> logout() {
        return new ResponseEntity<CommonResponse>(userService.signOut(), HttpStatus.OK);
    }

    @ApiOperation(value = "Forgot Password Api", nickname = "forgotPassword", notes = "Forgot Password Api", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/forgot-password", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> forgotPassword(@ApiParam(value = "AuthRequest object", required = true) @Valid @RequestBody AuthRequestDTO body) {
        return new ResponseEntity<CommonResponse>(userService.forgotPassword(body), HttpStatus.OK);
    }

    @ApiOperation(value = "Reset Password Api", nickname = "resetPassword", notes = "Reset Password Api", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/reset-password", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> resetPassword(@ApiParam(value = "AuthRequest object", required = true) @Valid @RequestBody AuthRequestDTO body) {
        return new ResponseEntity<CommonResponse>(userService.resetPassword(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get user settings", nickname = "getUserSettings", notes = "Get user settings", response = UserDTO.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/get-settings", produces = {"application/json"}, method = RequestMethod.GET)
    //@PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<UserDTO> getUserSettings() {
        return new ResponseEntity<UserDTO>(userService.getUserSettings(), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Change Password", nickname = "changePassword", notes = "Change Password", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/change-password", consumes = {"application/json"}, method = RequestMethod.PUT)
    //@PreAuthorize("@appSecurityUtilityService.hasAppAdminAccess(authentication.principal)")
    public ResponseEntity<Void> changePassword(@ApiParam(value = "ChangePasswordDTO object", required = true) @Valid @RequestBody UserDTO body) {
        userService.changePassword(body);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload user profile photo", nickname = "uploadUserProfile", notes = "Upload user profile photo", tags = {"user",})
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

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get User Activity", nickname = "getActivityLogs", notes = "Get User Activity", response = List.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/activity-log", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Page<ActivityLog>> getActivityLogs(@ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<ActivityLog>>(userService.getActivityLogs(pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a user", nickname = "addUser", notes = "Add a user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<UserVO> addUser(@ApiParam(value = "User object", required = true) @Valid @RequestBody User body) {
        return new ResponseEntity<UserVO>(userService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update user", nickname = "updateUser", notes = "Update user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid user supplied"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json"}, method = RequestMethod.PUT)
    public ResponseEntity<UserVO> updateUser(@ApiParam(value = "User object", required = true) @Valid @RequestBody User body) {
        return new ResponseEntity<UserVO>(userService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update status of user's", nickname = "updateStatus", notes = "Update status of user's", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/{ids}/{status}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Void> updateStatus(@NotNull @ApiParam(value = "User id's to update status", required = true) @Valid @PathVariable(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Status", required = true) @PathVariable("status") Status status) {
        userService.updateUsersStatus(status, ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by username", nickname = "getUserByUserName", notes = "Find user by username", response = UserVO.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid username supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/{userName}/findByUserName", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<UserVO> getUserByUserName(@ApiParam(value = "userName of user to return", required = true) @PathVariable("userName") String userName) {
        return new ResponseEntity<UserVO>(userService.getUserByUserName(userName), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by Id", nickname = "getUserById", notes = "Find user by Id", response = UserVO.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = "/{userId}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<UserVO> getUserById(@ApiParam(value = "id of user to return", required = true) @PathVariable("userId") BigInteger userId) {
        return new ResponseEntity<UserVO>(userService.getUserById(userId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by multi Id", nickname = "getUserByIds", notes = "Find user by multi Id", response = UserVO.class, responseContainer = "List", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List")
    })
    @RequestMapping(value = "/users/findByIds", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<Page<UserVO>> getUserByIds(@NotNull @ApiParam(value = "Ids to filter by", required = true) @Valid @RequestParam(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<Page<UserVO>>(userService.getUserByIds(ids, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Users", nickname = "getUsers", notes = "Get all Users", response = PaginatedList.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<UserVO>> getUsers(
            @ApiParam(value = "User Email", defaultValue = "", required = false) @RequestParam(value = "email", defaultValue = "", required = false) String email,
            @ApiParam(value = "User EmployeeId", defaultValue = "", required = false) @RequestParam(value = "employeeId", defaultValue = "", required = false) String employeeId,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.ASC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<UserVO>>(userService.getUsers(email, employeeId, Status.valueOf(status), pageable), HttpStatus.OK);
    }
}
