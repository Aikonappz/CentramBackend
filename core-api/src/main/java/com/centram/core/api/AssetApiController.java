package com.centram.core.api;


import com.centram.common.utility.AppSecurityUtilityService;
import com.centram.common.utility.PaginatedList;
import com.centram.common.view.Views;
import com.centram.core.service.AssetService;
import com.centram.domain.Asset;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
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

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "asset", description = "Asset Api")
@RequestMapping(value = "/api/v1/asset")
@Controller
public class AssetApiController {

    private static final Logger log = LoggerFactory.getLogger(AssetApiController.class);

    @Autowired
    private AppSecurityUtilityService appSecurityUtilityService;

    @Autowired
    private AssetService assetService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Save an asset", nickname = "save", notes = "Save an asset", tags = {"Asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.DetailView.class)
    @RequestMapping(value = "/", produces = {"application/json"}, consumes = {"application/json",}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','WRITE',authentication.principal)")
    public ResponseEntity<Asset> save(@ApiParam(value = "Asset object", required = true) @Valid @RequestBody Asset body) {
        return new ResponseEntity<Asset>(assetService.save(body), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Find asset by Id", nickname = "getAssetById", notes = "Find asset by Id", response = Asset.class, tags = {"Asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Asset.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView({Views.DetailView.class,})
    @RequestMapping(value = "/{assetId}", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','READ|WRITE|APPROVE',authentication.principal)")
    public ResponseEntity<Asset> getAssetById(@ApiParam(value = "id of asset to return", required = true) @PathVariable("assetId") BigInteger assetId) {
        return new ResponseEntity<Asset>(assetService.getAssetById(assetId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get assets", nickname = "getAssets", notes = "Get assets", response = PaginatedList.class, tags = {"Asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = PaginatedList.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.ListView.class)
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET,REQUESTED ASSET','READ|SEARCH,WRITE|SOLVE|ASSIGN',authentication.principal)")
    public ResponseEntity<PaginatedList<Asset>> getAssets(
            @ApiParam(value = "Product Category", defaultValue = "", required = false) @RequestParam(value = "productCategory", defaultValue = "", required = false) String productCategory,
            @ApiParam(value = "Asset Type", defaultValue = "", required = false) @RequestParam(value = "assetType", defaultValue = "", required = false) String assetType,
            @ApiParam(value = "model No", defaultValue = "", required = false) @RequestParam(value = "modelNo", defaultValue = "", required = false) String modelNo,
            @ApiParam(value = "serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "asset available", defaultValue = "-1", required = false) @RequestParam(value = "available", defaultValue = "-1", required = false) Integer available,
            @ApiParam(value = "Pageable parameters", required = false) @PageableDefault(size = Integer.MAX_VALUE, page = 0, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable
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
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload assets data csv", nickname = "uploadAssetsData", notes = "Upload assets data", tags = {"Asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 405, message = "Validation exception"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','WRITE',authentication.principal)")
    public ResponseEntity uploadAssetsData(@ApiParam(value = "Assets CSV file", required = true) @RequestParam(name = "file", required = true) MultipartFile multipartFile) throws IOException {
        assetService.uploadAssetsData(multipartFile);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * download asset in csv
     *
     * @return
     */
    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Downoad all Assets", nickname = "downloadAssets", notes = "Download all Assets", response = Resource.class, tags = {"Asset",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Resource.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @PreAuthorize("@appSecurityUtilityService.hasPermission('MANAGE ASSET','READ|SEARCH',authentication.principal)")
    public ResponseEntity<Resource> downloadAssets(
            @ApiParam(value = "Product Category", defaultValue = "", required = false) @RequestParam(value = "productCategory", defaultValue = "", required = false) String productCategory,
            @ApiParam(value = "Asset Type", defaultValue = "", required = false) @RequestParam(value = "assetType", defaultValue = "", required = false) String assetType,
            @ApiParam(value = "model No", defaultValue = "", required = false) @RequestParam(value = "modelNo", defaultValue = "", required = false) String modelNo,
            @ApiParam(value = "serial no", defaultValue = "", required = false) @RequestParam(value = "serialNo", defaultValue = "", required = false) String serialNo,
            @ApiParam(value = "asset available", defaultValue = "-1", required = false) @RequestParam(value = "available", defaultValue = "-1", required = false) Integer available
    ) {
        final InputStreamResource resource = new InputStreamResource(assetService.download(productCategory, assetType, modelNo, serialNo, available));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "assets-" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}