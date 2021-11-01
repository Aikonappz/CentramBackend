package com.centram.core.api;


import com.centram.core.service.MediaService;
import com.centram.domain.MediaFile;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Api(value = "users", description = "Media Api")
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
    @RequestMapping(value = "/upload-media", produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity<MediaFile> uploadMedia(HttpServletRequest request) {
        return new ResponseEntity<MediaFile>(mediaService.uploadMediaFile(request), HttpStatus.OK);
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

    /*@ApiOperation(authorizations = {@Authorization(value = "JWT")}, value = "Get all medias by product name", nickname = "getMediasByProductName", notes = "Get all medias by product name", response = List.class, tags = {"media",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Media not found")
    })
    @RequestMapping(value = "/{productName}", method = RequestMethod.GET)
    public ResponseEntity<List<MediaFile>> getMediasByProductName(@ApiParam(value = "Product name", required = true) @PathVariable("productName") String productName) {
        return new ResponseEntity<List<MediaFile>>(mediaService.getMediasByProductName(productName), HttpStatus.OK);
    }*/
}
