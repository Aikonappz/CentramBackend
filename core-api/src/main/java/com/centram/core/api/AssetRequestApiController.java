package com.centram.core.api;


import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.AssetRequestService;
import com.centram.domain.AssetRequest;
import com.centram.domain.User;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "assset request", description = "Asset Request Api")
@RequestMapping(value = "/api/v1/asset-request")
@Controller
public class AssetRequestApiController {

    private static final Logger log = LoggerFactory.getLogger(AssetRequestApiController.class);

    @Autowired
    private AssetRequestService assetRequestService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save an asset request", nickname = "save", notes = "Save an asset request", tags = {"asset request",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REQUEST ASSET','WRITE',authentication.principal)")
    public ResponseEntity<AssetRequest> save(@ApiParam(value = "AssetRequest object", required = true) @Valid @RequestBody AssetRequest body) {
        return new ResponseEntity<AssetRequest>(assetRequestService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find asset request by Id", nickname = "getAssetRequestById", notes = "Find asset request by Id", response = AssetRequest.class, tags = {"asset request",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "incident not found")
    })
    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{assetRequestId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REQUEST ASSET','READ|WRITE|APPROVE',authentication.principal)")
    public ResponseEntity<AssetRequest> getAssetById(@ApiParam(value = "id of asset request to return", required = true) @PathVariable("assetRequestId") BigInteger assetRequestId) {
        return new ResponseEntity<AssetRequest>(assetRequestService.getAssetRequest(assetRequestId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all asset request", nickname = "getAssetRequests", notes = "Get all asset requsts", response = PaginatedList.class, tags = {"asset request",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('REQUEST ASSET','READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<AssetRequest>> getAssetRequests(
            @ApiParam(value = "Product Category", defaultValue = "-1", required = false) @RequestParam(value = "productCategory", defaultValue = "-1", required = false) Integer productCategory,
            @ApiParam(value = "Asset Type", defaultValue = "-1", required = false) @RequestParam(value = "assetType", defaultValue = "-1", required = false) Integer assetType,
            @ApiParam(value = "model No", defaultValue = "", required = false) @RequestParam(value = "modelNo", defaultValue = "", required = false) String modelNo,
            @ApiParam(value = "serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "approved", defaultValue = "-1", required = false) @RequestParam(value = "approved", defaultValue = "-1", required = false) Integer approved,
            @ApiParam(value = "allocated", defaultValue = "-1", required = false) @RequestParam(value = "allocated", defaultValue = "-1", required = false) Integer allocated,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<AssetRequest>>(assetRequestService.getAssetRequests(productCategory, assetType, modelNo, serialNo, approved, allocated, pageable), HttpStatus.OK);
    }
}