package com.centram.core.api;


import com.centram.common.utility.PaginatedList;
import com.centram.core.service.PositionService;
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

@RequestMapping(value = "/api/v1/position")
@RestController
public class PositionController {

    @Autowired
    private PositionService positionService;




    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<PaginatedList<Position>> organisationReport(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<Position>>(positionService.getAll(name, Status.valueOf(status), pageable), HttpStatus.OK);
    }


    /**
     * @param body
     * @return
     */
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    public ResponseEntity<Position> save(@RequestBody Position body) {
        return new ResponseEntity<Position>(positionService.save(body), HttpStatus.OK);
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}",  method = RequestMethod.GET)
    public ResponseEntity<Position> getById(@PathVariable BigInteger id) {
        return new ResponseEntity<Position>(positionService.getById(id), HttpStatus.OK);
    }


}
