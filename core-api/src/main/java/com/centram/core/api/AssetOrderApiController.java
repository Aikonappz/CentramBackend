package com.centram.core.api;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.AssetOrderService;
import com.centram.domain.AssetOrder;
import com.centram.domain.Incident;
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
@Api(value = "Assset Order", description = "Asset Order Api")
@RequestMapping(value = "/api/v1/asset-order")
@Controller
public class AssetOrderApiController {

    private static final Logger log = LoggerFactory.getLogger(AssetOrderApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private AssetOrderService assetOrderService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save an asset order", nickname = "save", notes = "Save an asset order", tags = {"Asset Order",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET','WRITE',authentication.principal)")
    public ResponseEntity<AssetOrder> save(@ApiParam(value = "Asset Order object", required = true) @Valid @RequestBody AssetOrder body) {
        return new ResponseEntity<AssetOrder>(assetOrderService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find asset order by Id", nickname = "getAssetOrderById", notes = "Find asset order by Id", response = Incident.class, tags = {"Asset Order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = AssetOrder.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{assetOrderId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET','READ|WRITE|APPROVE',authentication.principal)")
    public ResponseEntity<AssetOrder> getAssetOrderById(@ApiParam(value = "id of asset order to return", required = true) @PathVariable("assetOrderId") BigInteger assetOrderId) {
        return new ResponseEntity<AssetOrder>(assetOrderService.getAssetOrderById(assetOrderId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all ordered assets", nickname = "getOrderedAssets", notes = "Get all ordered assets", response = PaginatedList.class, tags = {"Asset Order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET','READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<AssetOrder>> getOrderedAssets(
            @ApiParam(value = "order no", defaultValue = "", required = false) @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<AssetOrder>>(assetOrderService.getOrderedAssets(orderNo, status, pageable), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Approve an asset order", nickname = "assetOrderAction", notes = "Approve an asset order", tags = {"Asset Order",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET','APPROVE',authentication.principal)")
    public ResponseEntity<AssetOrder> assetOrderAction(@ApiParam(value = "AssetApprovalDTO object", required = true) @Valid @RequestBody AssetApprovalDTO body) {
        return new ResponseEntity<AssetOrder>(assetOrderService.assetOrderAction(body), HttpStatus.OK);
    }
}