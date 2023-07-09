package com.centram.core.api;


import com.centram.common.dto.AssetApprovalDTO;
import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.AssetOrderService;
import com.centram.domain.AssetOrder;
import com.centram.domain.Incident;
import com.fasterxml.jackson.annotation.JsonView;

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



@RequestMapping(value = "/api/v1/asset-order")
@Controller
public class AssetOrderApiController {

    private static final Logger log = LoggerFactory.getLogger(AssetOrderApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private AssetOrderService assetOrderService;


    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET','WRITE',authentication.principal)")
    public ResponseEntity<AssetOrder> save( @Valid @RequestBody AssetOrder body) {
        return new ResponseEntity<AssetOrder>(assetOrderService.save(body), HttpStatus.OK);
    }


    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{assetOrderId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET,ORDERED ASSET ACTION','READ|WRITE|APPROVE,READ|APPROVE',authentication.principal)")
    public ResponseEntity<AssetOrder> getAssetOrderById( @PathVariable("assetOrderId") BigInteger assetOrderId) {
        return new ResponseEntity<AssetOrder>(assetOrderService.getAssetOrderById(assetOrderId), HttpStatus.OK);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET','READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<AssetOrder>> getOrderedAssets(
             @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
             @RequestParam(value = "status", defaultValue = "", required = false) String status,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<AssetOrder>>(assetOrderService.getOrderedAssets(orderNo, status, pageable), HttpStatus.OK);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all-asset-order-for-approval", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDERED ASSET ACTION','READ|SEARCH',authentication.principal)")
    public ResponseEntity<PaginatedList<AssetOrder>> getOrderedAssetsForApproval(
             @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
             @RequestParam(value = "status", defaultValue = "", required = false) String status,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<AssetOrder>>(assetOrderService.getOrderedAssetsForApproval(orderNo, status, pageable), HttpStatus.OK);
    }


    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.PUT)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('ORDER ASSET,ORDERED ASSET ACTION','WRITE|APPROVE,WRITE|APPROVE',authentication.principal)")
    public ResponseEntity<AssetOrder> assetOrderAction( @Valid @RequestBody AssetApprovalDTO body) {
        return new ResponseEntity<AssetOrder>(assetOrderService.assetOrderAction(body), HttpStatus.OK);
    }
}