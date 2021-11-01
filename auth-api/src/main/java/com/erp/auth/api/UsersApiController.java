package com.erp.auth.api;

import com.erp.auth.api.interfaces.IUsersApi;
import com.erp.auth.service.UserService;
import com.erp.common.dto.AuthRequestDTO;
import com.erp.common.dto.LoggedInUserDTO;
import com.erp.common.utility.JwtTokenUtil;
import com.erp.domain.User;
import com.erp.domain.enumarator.Status;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "users", description = "the users API")
@RequestMapping(value = "/api/v1/user")
@Controller
public class UsersApiController{

    private static final Logger log = LoggerFactory.getLogger(UsersApiController.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @ApiOperation(value = "Login Api", nickname = "login", notes = "Login user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @RequestMapping(value = "/login",
            produces = {"application/json"},
            consumes = {"application/json",},
            method = RequestMethod.POST)
    public ResponseEntity<LoggedInUserDTO> login(@ApiParam(value = "AuthRequest object that needs to be here", required = true) @Valid @RequestBody AuthRequestDTO body) {
        try {
            LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())).getPrincipal();
            HttpHeaders responseHeaders = new HttpHeaders() {{
                set("Authorization", jwtTokenUtil.generateToken(loggedInUserDTO, true));
            }};
            return ResponseEntity.ok().headers(responseHeaders).body(loggedInUserDTO);
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Add a new user", nickname = "addUser", notes = "Add a new user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")})
    @RequestMapping(value = "/",
            produces = {"application/json"},
            consumes = {"application/json",},
            method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@ApiParam(value = "User object that needs to be added", required = true) @Valid @RequestBody User body) {
        return new ResponseEntity<User>(userService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Update an existing user", nickname = "updateUser", notes = "Update an existing user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid user supplied"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 405, message = "Validation exception")})
    @RequestMapping(value = "/",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@ApiParam(value = "User object that needs to update", required = true) @Valid @RequestBody User body) {
        return new ResponseEntity<User>(userService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Deletes a user", nickname = "deleteUser", notes = "Deletes a user", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "User not found")})
    @RequestMapping(value = "/users/{userId}",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@ApiParam(value = "User id to delete", required = true) @PathVariable("userId") BigInteger userId) {
        userService.updateStatus(Status.fromValue("INACTIVE"), userId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by username", nickname = "getUserByUserName", notes = "Find user by username", response = User.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid email supplied"),
            @ApiResponse(code = 404, message = "User not found")})
    @RequestMapping(value = "/users/{userName}/findByUserName",
            produces = {"application/json"},
            method = RequestMethod.GET)

    public ResponseEntity<User> getUserByUserName(@ApiParam(value = "user_name of user to return", required = true) @PathVariable("email") String username) {
        return new ResponseEntity<User>(userService.getUserByUserName(username), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by Id", nickname = "getUserById", notes = "Find user by Id", response = User.class, tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "User not found")})
    @RequestMapping(value = "/users/{userId}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@ApiParam(value = "id of user to return", required = true) @PathVariable("userId") BigInteger userId) {
        return new ResponseEntity<User>(userService.getUserById(userId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find user by multi Id", nickname = "getUserByIds", notes = "Find user by multi Id", response = User.class, responseContainer = "List", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List")})
    @RequestMapping(value = "/users/findByIds",
            produces = {"application/json"},
            method = RequestMethod.GET)

    public ResponseEntity<Page<User>> getUserByIds(@NotNull @ApiParam(value = "Ids to filter by", required = true) @Valid @RequestParam(value = "ids", required = true) List<BigInteger> ids, @ApiParam(value = "Pageable parameters", required = false) Pageable pageable) {
        return new ResponseEntity<Page<User>>(userService.getUserByIds(ids, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all Users", nickname = "getUsers", notes = "Get all Users", response = User.class, responseContainer = "List", tags = {"user",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")})
    @RequestMapping(value = "/users",
            produces = {"application/json"},
            method = RequestMethod.GET)

    public ResponseEntity<Page<User>> getUsers(@ApiParam(value = "Pageable parameters", required = false) Pageable pageable) {
        return new ResponseEntity<Page<User>>(userService.getUsers(pageable), HttpStatus.OK);
    }
}
