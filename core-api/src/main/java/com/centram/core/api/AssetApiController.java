package com.centram.core.api;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.AssetService;
import com.centram.domain.Asset;
import com.centram.domain.Incident;
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
@Api(value = "assset", description = "Asset Api")
@RequestMapping(value = "/api/v1/asset")
@Controller
public class AssetApiController {

    private static final Logger log = LoggerFactory.getLogger(AssetApiController.class);

    @Autowired
    private AssetService assetService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save an asset order", nickname = "save", notes = "Save an asset order", tags = {"asset order",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ADD ASSET','WRITE',authentication.principal)")
    public ResponseEntity<Asset> save(@ApiParam(value = "Asset object", required = true) @Valid @RequestBody Asset body) {
        return new ResponseEntity<Asset>(assetService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Approve an asset", nickname = "approveAsset", notes = "Approve an asset", tags = {"asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','APPROVE',authentication.principal)")
    public ResponseEntity<Asset> approveAsset(@ApiParam(value = "Asset object", required = true) @Valid @RequestBody AssetApprovalDTO body) {
        return new ResponseEntity<Asset>(assetService.approveAsset(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find asset by Id", nickname = "getAssetById", notes = "Find asset by Id", response = Incident.class, tags = {"asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 400, message = "Invalid name supplied"),
            @ApiResponse(code = 404, message = "incident not found")
    })
    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{assetId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET,ADD ASSET','READ,READ|WRITE|APPROVE',authentication.principal)")
    public ResponseEntity<Asset> getAssetById(@ApiParam(value = "id of asset to return", required = true) @PathVariable("assetOrderId") BigInteger assetId) {
        return new ResponseEntity<Asset>(assetService.getAssetById(assetId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all assets", nickname = "getAssets", notes = "Get all assets", response = PaginatedList.class, tags = {"asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = PaginatedList.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<Asset>> getAssets(
            @ApiParam(value = "asset no", defaultValue = "", required = false) @RequestParam(value = "assetNo", defaultValue = "", required = false) String assetNo,
            @ApiParam(value = "Status", defaultValue = "ALL", required = false) @RequestParam(value = "status", defaultValue = "", required = false) String status,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Asset>>(assetService.getAssets(assetNo, status, pageable), HttpStatus.OK);
    }
}