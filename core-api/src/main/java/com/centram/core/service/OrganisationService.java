package com.centram.core.service;

import com.centram.common.dto.LoggedInUserDTO;
import com.centram.common.dto.OrganisationDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.Utility;
import com.centram.core.dao.OrganisationDao;
import com.centram.domain.ActivityLog;
import com.centram.domain.MediaFile;
import com.centram.domain.Organisation;
import com.centram.domain.User;
import com.centram.domain.enumarator.ActivityType;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.centram.domain.enumarator.Status;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

@Service
public class OrganisationService {

    private static final Logger log = LoggerFactory.getLogger(OrganisationService.class);
    private final String defaultPassword = "sample";
    @Autowired
    private RedisService redisService;
    @Autowired
    private OrganisationDao organisationDao;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MediaService mediaService;

    @Autowired
    private ActivityLogService activityLogService;

    public Organisation save(Organisation organisation) {
        organisation.setMnemonic(Utility.getUniqueString(10));
        organisation.setStatus(Status.ACTIVE);
        Organisation newOrganisation = organisationDao.save(organisation);
        User user = new User();
        user.setFirstName(newOrganisation.getName());
        user.setLastName(newOrganisation.getName());
        user.setStatus(Status.ACTIVE);
        user.setAddresses(newOrganisation.getAddresses());
        user.setBankDetails(newOrganisation.getBankDetails());
        user.setContacts(newOrganisation.getContacts());
        user.setRoles(Arrays.asList(BigInteger.valueOf(Long.valueOf(2))));
        user.setOrganisation(newOrganisation);
        userService.save(user);
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.ORGANISATION_ONBOARD));
        return redisService.redisOperation(newOrganisation.getId(), Utility.initializeAndUnproxy(newOrganisation));
    }

    public Organisation update(Organisation organisation) {
        Organisation newOrganisation = organisationDao.update(organisation);
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.UPDATE_ORGANISATION));
        return redisService.redisOperation(newOrganisation.getId(), Utility.initializeAndUnproxy(newOrganisation));
    }

    public void updateStatus(Status status, BigInteger organisationId) {
        organisationDao.updateStatus(status, organisationId);
        Organisation organisation = this.getOrganisationById(organisationId);
        organisation.setStatus(status);
        redisService.redisOperation(organisationId, Utility.initializeAndUnproxy(organisation));
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.UPDATE_ORGANISATION));
    }

    public Organisation getOrganisationById(BigInteger organisationId) {
        Organisation organisation = redisService.getCachedOrganisation(organisationId);
        if (organisation == null) {
            organisation = organisationDao.getOrganisationById(organisationId);
            redisService.redisOperation(organisation.getId(), Utility.initializeAndUnproxy(organisation));
        }
        return organisation;
    }

    public OrganisationDTO getOrganisationSettings() {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrganisationDTO organisationDTO = new OrganisationDTO();
        Organisation organisation = this.getOrganisationById(loggedInUserDTO.getOrganisationId());
        organisationDTO.setMediaFile(organisationDao.getOrganisationLogo(loggedInUserDTO.getOrganisationId()));
        organisationDTO.setSetting(organisation.getSetting());
        return organisationDTO;
    }

    public void updateOrganisationSettings(OrganisationDTO organisationDTO) {
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organisation organisation = redisService.getCachedOrganisation(loggedInUserDTO.getOrganisationId());
        organisation.setSetting(organisationDTO.getSetting());
        organisationDao.updateSetting(organisationDTO.getSetting(), loggedInUserDTO.getOrganisationId());
        redisService.redisOperation(organisation.getId(), Utility.initializeAndUnproxy(organisation));
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.UPDATE_ORGANISATION));
    }

    public OrganisationDTO uploadOrganisationLogo(HttpServletRequest request) {
        OrganisationDTO organisationDTO = new OrganisationDTO();
        LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        MediaFile mediaFile = organisationDao.getOrganisationLogo(loggedInUserDTO.getOrganisationId());
        mediaFile = (mediaFile == null) ? new MediaFile() : mediaFile;
        mediaFile.setEntityId(loggedInUserDTO.getOrganisationId());
        mediaFile.setEntityType(EntityType.ORGANISATION);
        mediaFile.setMediaType(MediaType.ORGANISATION_LOGO_IMAGE);
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
                    //OutputStream out = new FileOutputStream(filename);
                    //IOUtils.copy(stream, out);
                    stream.close();
                    //out.close();
                }
            }
        } catch (FileUploadException e) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        } catch (IOException e) {
            throw new AppException(GenericErrorCode.UNKNOWN_ERROR);
        }
        organisationDTO.setMediaFile(mediaService.save(mediaFile));
        activityLogService.save(new ActivityLog(loggedInUserDTO.getUserId(), (loggedInUserDTO.getOrganisationId() != null) ? loggedInUserDTO.getOrganisationId() : null, ActivityType.ORGANISATION_LOGO_UPLOAD));
        return organisationDTO;
    }

    public Organisation getOrganisationByMnemonic(String mnemonic) {
        Organisation organisation = redisService.getCachedOrganisation(mnemonic);
        if (organisation == null) {
            organisation = organisationDao.getOrganisationByMnemonic(mnemonic);
            redisService.redisOperation(organisation.getId(), Utility.initializeAndUnproxy(organisation));
        }
        return organisation;
    }

    public Page<Organisation> getOrganisations(Pageable pageable) {
        return organisationDao.getOrganisations(pageable);
    }
}