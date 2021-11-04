package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.core.repository.MediaFileRepository;
import com.centram.domain.MediaFile;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Service
public class MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaService.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Transactional(readOnly = true)
    public MediaFile getById(BigInteger mediaId) {
        MediaFile mediaFile = mediaFileRepository.getOne(mediaId);
        if (mediaFile == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return mediaFile;
    }

    @Transactional(readOnly = true)
    public MediaFile getMediaFile(EntityType entityType, MediaType mediaType, BigInteger entityId) {
        MediaFile mediaFile = mediaFileRepository.getMediaFile(entityType, mediaType, entityId);
        /*if (mediaFile == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }*/
        return mediaFile;
    }

    @Transactional
    public MediaFile save(MediaFile mediaFile) {
        return mediaFileRepository.save(mediaFile);
    }

    @Transactional
    public void delete(BigInteger mediaId) {
        mediaFileRepository.deleteById(mediaId);
    }

    /*public List<MediaFile> getMediasByProductName(String productName) {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mediaFileRepository.getMediasByProductName(loggedInUserDTO.getOrganisationId(), productName);
    }*/

    public MediaFile uploadMediaFile(HttpServletRequest request) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        MediaFile mediaFile = new MediaFile();
        mediaFile.setEntityId(null);
        mediaFile.setEntityType(null);
        mediaFile.setMediaType(null);
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator itemIterator = upload.getItemIterator(request);
            while (itemIterator.hasNext()) {
                FileItemStream item = itemIterator.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    String filename = item.getName();
                    mediaFile.setFileName(filename);
                    mediaFile.setFileType(new MimetypesFileTypeMap().getContentType(filename));
                    mediaFile.setContent(IOUtils.toByteArray(stream));
                    /*OutputStream out = new FileOutputStream(filename);
                    IOUtils.copy(stream, out);
                    out.close();*/
                    stream.close();
                }
            }
        } catch (FileUploadException e) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
        }
        return this.save(mediaFile);
    }
}
