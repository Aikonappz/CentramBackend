package com.centram.core.api;


import com.centram.common.utility.PaginatedList;
import com.centram.core.service.RequisitionService;
import com.centram.domain.Requisition;
import com.centram.domain.RequisitionManagerReview;
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


    @RequestMapping(value = "/add", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<Requisition> saveRequisition(@RequestBody Requisition requisition) {
        return new ResponseEntity<Requisition>(requisitionService.saveRequisition(requisition), HttpStatus.OK);
    }


    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Requisition>> organisationReport(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Requisition>>(requisitionService.getAllRequisition(name, Status.valueOf(status), pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Requisition> getByRequisitionId(@PathVariable BigInteger id) {
        Requisition requisition = requisitionService.getByRequisitionId(id);
        return new ResponseEntity<>(requisition, HttpStatus.OK);
    }

    @PostMapping(value = "/manager_review/add", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<RequisitionManagerReview> saveRequisitionManagerReview(@RequestBody RequisitionManagerReview body) {
        return new ResponseEntity<>(requisitionService.saveRequisitionManagerReview(body), HttpStatus.OK);
    }

    @GetMapping(value = "/manager_review/{id}", produces = "application/json")
    public ResponseEntity<RequisitionManagerReview> getByRequisitionMangerReviewId(@PathVariable BigInteger id) {
        return new ResponseEntity<>(requisitionService.getByRequisitionMangerReviewId(id), HttpStatus.OK);
    }

}
