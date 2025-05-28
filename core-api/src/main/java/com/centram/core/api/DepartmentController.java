package com.centram.core.api;


import com.centram.common.dto.CommonProjection;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.DepartmentService;
import com.centram.domain.Department;
import com.centram.domain.Position;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RequestMapping(value = "/api/v1/department")
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<CommonProjection>> organisationReport(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<CommonProjection>>(departmentService.getAll(name, Status.valueOf(status), pageable), HttpStatus.OK);
    }


    /**
     * @param body
     * @return
     */
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<Department> save(@RequestBody Department body) {
        return new ResponseEntity<Department>(departmentService.save(body), HttpStatus.OK);
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Department> getById(@PathVariable BigInteger id) {
        return new ResponseEntity<Department>(departmentService.getById(id), HttpStatus.OK);
    }


}
