package com.centram.core.api;

import com.centram.common.dto.CommonProjection;
import com.centram.common.utility.PaginatedList;
import com.centram.core.service.LocationService;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/location")
public class LocationController {

    @Autowired
    LocationService locationService;

    @GetMapping(value = "/")
    public ResponseEntity<PaginatedList<CommonProjection>> organisationReport(@RequestParam(value = "name", defaultValue = "", required = false) String name, @RequestParam(value = "status", defaultValue = "ALL", required = false) String status, @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
        return new ResponseEntity<PaginatedList<CommonProjection>>(locationService.getAll(name, Status.valueOf(status), pageable), HttpStatus.OK);
    }
}
