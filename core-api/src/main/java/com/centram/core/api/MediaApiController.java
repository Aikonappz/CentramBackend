package com.centram.core.api;


import com.centram.common.view.Views;
import com.centram.core.service.MediaService;
import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.EntityType;
import com.fasterxml.jackson.annotation.JsonView;

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
import java.util.List;



@RequestMapping(value = "/api/v1/media")
@Controller
public class MediaApiController {

    private static final Logger log = LoggerFactory.getLogger(MediaApiController.class);

    @Autowired
    private MediaService mediaService;


    @RequestMapping(value = "/upload-media/{entityId}/{entityType}/{mediaType}/{chatRoomId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    public ResponseEntity<List<MediaFile>> uploadMedia(
             @PathVariable("entityId") BigInteger entityId,
             @PathVariable("entityType") EntityType entityType,
             @PathVariable("mediaType") com.centram.domain.enumarator.MediaType mediaType,
             @PathVariable("chatRoomId") String chatRoomId,
             @RequestParam(name = "file", required = true) MultipartFile[] multipartFiles
    ) {
        return new ResponseEntity<List<MediaFile>>(
                mediaService.uploadMediaFile(entityId, entityType, mediaType, chatRoomId, multipartFiles),
                HttpStatus.OK
        );
    }


    @RequestMapping(value = "/{mediaId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMedia( @PathVariable("mediaId") BigInteger mediaId) {
        mediaService.delete(mediaId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @RequestMapping(value = "/{mediaId}", method = RequestMethod.GET)
    @JsonView({Views.BasicView.class})
    public ResponseEntity<MediaFile> getMediaById( @PathVariable("mediaId") BigInteger mediaId) {
        return new ResponseEntity<MediaFile>(mediaService.getById(mediaId), HttpStatus.OK);
    }


    @RequestMapping(value = "/{mediaId}/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadMedia( @PathVariable("mediaId") BigInteger mediaId) {
        MediaFile mediaFile = mediaService.getById(mediaId);
        final InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(mediaFile.getContent()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mediaFile.getFileName())
                .contentType(MediaType.parseMediaType(mediaFile.getFileType()))
                .body(resource);
    }
}
