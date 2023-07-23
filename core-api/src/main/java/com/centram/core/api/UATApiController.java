package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.AssetService;
import com.centram.core.service.ProjectUatService;
import com.centram.domain.Asset;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigInteger;


@RequestMapping(value = "/api/v1/uat")
@Controller
public class UATApiController {

    private static final Logger log = LoggerFactory.getLogger(UATApiController.class);

    @Autowired
    private ProjectUatService projectUatService;

    @Autowired
    private AssetService assetService;


    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','WRITE',authentication.principal)")
    public ResponseEntity<Asset> save( @Valid @RequestBody Asset body) {
        return new ResponseEntity<Asset>(assetService.save(body), HttpStatus.OK);
    }


    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{assetId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','READ|WRITE|APPROVE',authentication.principal)")
    public ResponseEntity<Asset> getAssetById( @PathVariable("assetId") BigInteger assetId) {
        return new ResponseEntity<Asset>(assetService.getAssetById(assetId), HttpStatus.OK);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET,REQUESTED ASSET','READ|SEARCH,WRITE|SOLVE|ASSIGN',authentication.principal) || @appSecurityUtilityService.hasOrgAdminAccess(authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<Asset>> getAssets(
             @RequestParam(value = "productCategory", defaultValue = "", required = false) String productCategory,
             @RequestParam(value = "assetType", defaultValue = "", required = false) String assetType,
             @RequestParam(value = "modelNo", defaultValue = "", required = false) String modelNo,
             @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
             @RequestParam(value = "available", defaultValue = "-1", required = false) Integer available,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Asset>>(assetService.getAssets(productCategory, assetType, modelNo, serialNo, available, pageable), HttpStatus.OK);
    }

    /**
     * upload assets in csv
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */

    @RequestMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','WRITE',authentication.principal)")
    public ResponseEntity uploadAssetsData( @RequestParam(name = "file", required = true) MultipartFile multipartFile) throws IOException {
        assetService.uploadAssetsData(multipartFile);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * download asset in csv
     *
     * @return
     */

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','READ|SEARCH',authentication.principal)")
    public ResponseEntity<Resource> downloadAssets(
             @RequestParam(value = "productCategory", defaultValue = "", required = false) String productCategory,
             @RequestParam(value = "assetType", defaultValue = "", required = false) String assetType,
             @RequestParam(value = "modelNo", defaultValue = "", required = false) String modelNo,
             @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
             @RequestParam(value = "available", defaultValue = "-1", required = false) Integer available
    ) {
        final InputStreamResource resource = new InputStreamResource(assetService.download(productCategory, assetType, modelNo, serialNo, available));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "assets-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }


    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/available-asset", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET,REQUESTED ASSET','READ|SEARCH,WRITE|SOLVE|ASSIGN',authentication.principal) || @appSecurityUtilityService.hasOrgAdminAccess(authentication.principal) || @appSecurityUtilityService.hasCategoryAdminAccess(authentication.principal)")
    public ResponseEntity<PaginatedList<Asset>> getAvailableAssets(
             @RequestParam(value = "productCategory", defaultValue = "", required = false) String productCategory,
             @RequestParam(value = "assetType", defaultValue = "", required = false) String assetType,
             @RequestParam(value = "requestRaisedId", required = false) BigInteger requestRaisedId,
             @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
    ) {
        return new ResponseEntity<PaginatedList<Asset>>(assetService.getAvailableAssets(productCategory, assetType, requestRaisedId, pageable), HttpStatus.OK);
    }
}