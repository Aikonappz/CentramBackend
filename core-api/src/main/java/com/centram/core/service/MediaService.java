package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.MediaFileRepository;
import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaService.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Transactional(readOnly = true)
    public MediaFile getById(BigInteger mediaId) {
        Optional<MediaFile> mediaFile = mediaFileRepository.findById(mediaId);
        if (!mediaFile.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return mediaFile.get();
    }

    @Transactional(readOnly = true)
    public MediaFile getMediaFile(EntityType entityType, MediaType mediaType, BigInteger entityId) {
        MediaFile mediaFile = mediaFileRepository.getMediaFile(entityType, mediaType, entityId);
        /*if (mediaFile == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }*/
        return mediaFile;
    }

    @Transactional(readOnly = true)
    public List<MediaFile> getMediaFiles(BigInteger entityId, EntityType entityType, MediaType mediaType) {
        return mediaFileRepository.getMediaFiles(entityId, entityType, mediaType);
    }

    @Transactional(readOnly = false)
    public void delete(BigInteger mediaId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        mediaFileRepository.deleteById(mediaId);
    }

    @Transactional(readOnly = false)
    public void uploadMediaFile(BigInteger entityId, EntityType entityType, MediaType mediaType, MultipartFile[] multipartFiles) {
        LoggedInUser loggedInUserDTO = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (multipartFiles.length > 0) {
            try {
                List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
                MediaFile mediaFile = new MediaFile();
                for (MultipartFile multipartFile : multipartFiles) {
                    if (multipartFile.getSize() == 0) {
                        continue;
                    }
                    mediaFile = new MediaFile();
                    mediaFile.setEntityId(entityId);
                    mediaFile.setEntityType(entityType);
                    mediaFile.setFileName(multipartFile.getOriginalFilename());
                    mediaFile.setFileType(new MimetypesFileTypeMap().getContentType(multipartFile.getOriginalFilename()));
                    mediaFile.setContent(multipartFile.getBytes());
                    mediaFile.setMediaType(mediaType);
                    mediaFileList.add(mediaFile);
                }
                mediaFileRepository.saveAll(mediaFileList);
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
            }
        } else {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
    }
}