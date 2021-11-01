package com.erp.auth.api;

import com.erp.auth.api.interfaces.IPermissionApi;
import com.erp.auth.service.PermissionService;
import com.erp.domain.Permission;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.math.BigInteger;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")

@Controller
public class PermissionApiController implements IPermissionApi {

    private static final Logger log = LoggerFactory.getLogger(PermissionApiController.class);

    @Autowired
    private PermissionService permissionService;

    public ResponseEntity<Permission> addPermission(@ApiParam(value = "Permission object that needs to be added", required = true) @Valid @RequestBody Permission body) {
        return new ResponseEntity<Permission>(permissionService.save(body), HttpStatus.OK);
    }

    public ResponseEntity<Permission> updatePermission(@ApiParam(value = "Permission object that needs to be added to the store", required = true) @Valid @RequestBody Permission body) {
        return new ResponseEntity<Permission>(permissionService.save(body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<Permission>> getPermissionByRoleId(BigInteger roleId, Pageable pageable) {
        return new ResponseEntity<Page<Permission>>(permissionService.getPermissionByRoleId(roleId, pageable), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<Permission>> getPermissions(Pageable pageable) {
        return new ResponseEntity<Page<Permission>>(permissionService.getPermissions(pageable), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Permission> getPermission(BigInteger permissionId) {
        return new ResponseEntity<Permission>(permissionService.getPermissionById(permissionId), HttpStatus.OK);
    }

}
