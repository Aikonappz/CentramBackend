package com.centram.core.api;


import com.centram.common.view.Views;
import com.centram.core.service.MediaService;
import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.EntityType;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "media", description = "Media Api")
@RequestMapping(value = "/api/v1/media")
@Controller
public class MediaApiController {

    private static final Logger log = LoggerFactory.getLogger(MediaApiController.class);

    @Autowired
    private MediaService mediaService;

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Upload media", nickname = "uploadMedia", notes = "Upload media", tags = {"media",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid organisation supplied"),
            @ApiResponse(code = 404, message = "Media not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @RequestMapping(value = "/upload-media/{entityId}/{entityType}/{mediaType}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    public ResponseEntity uploadMedia(
            @ApiParam(value = "Media id", required = true) @PathVariable("entityId") BigInteger entityId,
            @ApiParam(value = "Entity type", required = true) @PathVariable("entityType") EntityType entityType,
            @ApiParam(value = "Media type", required = true) @PathVariable("mediaType") com.centram.domain.enumarator.MediaType mediaType,
            @ApiParam(value = "Users CSV file", required = true) @RequestParam(name = "file", required = true) MultipartFile[] multipartFiles
    ) {
        mediaService.uploadMediaFile(entityId, entityType, mediaType, multipartFiles);
        return new ResponseEntity<MediaFile>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Delete a media", nickname = "deleteMedia", notes = "Delete a media", response = Void.class, tags = {"media",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Media not found")
    })
    @RequestMapping(value = "/{mediaId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMedia(@ApiParam(value = "Media id", required = true) @PathVariable("mediaId") BigInteger mediaId) {
        mediaService.delete(mediaId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get a media", nickname = "getMediaById", notes = "Get a media", response = MediaFile.class, tags = {"media",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Media not found")
    })
    @RequestMapping(value = "/{mediaId}", method = RequestMethod.GET)
    @JsonView({Views.BasicView.class})
    public ResponseEntity<MediaFile> getMediaById(@ApiParam(value = "Media id", required = true) @PathVariable("mediaId") BigInteger mediaId) {
        return new ResponseEntity<MediaFile>(mediaService.getById(mediaId), HttpStatus.OK);
    }

    @ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Download a media", nickname = "downloadMedia", notes = "Download a media", response = Resource.class, tags = {"media",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Media not found")
    })
    @RequestMapping(value = "/{mediaId}/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadMedia(@ApiParam(value = "Media id", required = true) @PathVariable("mediaId") BigInteger mediaId) {
        MediaFile mediaFile = mediaService.getById(mediaId);
        final InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(mediaFile.getContent()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mediaFile.getFileName())
                .contentType(MediaType.parseMediaType(mediaFile.getFileType()))
                .body(resource);
    }
}
