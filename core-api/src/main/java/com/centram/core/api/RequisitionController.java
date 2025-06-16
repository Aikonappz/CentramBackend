package com.centram.core.api;


import com.centram.common.utility.PaginatedList;
import com.centram.core.service.RequisitionService;
import com.centram.domain.Requisition;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RequestMapping(value = "/api/v1/requisition")
@Controller
public class RequisitionController {

    @Autowired
    private RequisitionService requisitionService;


    /**
     * @param body
     * @return
     */
    @RequestMapping(value = "/add", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<Requisition> save(@RequestBody Requisition body) {
        return new ResponseEntity<Requisition>(requisitionService.save(body), HttpStatus.OK);
    }


    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Requisition>> organisationReport(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Requisition>>(requisitionService.getAll(name, Status.valueOf(status), pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Requisition> getById(@PathVariable BigInteger id) {
        Requisition requisition = requisitionService.findById(id);
        return new ResponseEntity<>(requisition, HttpStatus.OK);
    }

}
