package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.OrganisationDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.OrganisationRepository;
import com.centram.domain.ActivityLog;
import com.centram.domain.MediaFile;
import com.centram.domain.Organisation;
import com.centram.domain.enumarator.ActivityType;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {

    private static final Logger log = LoggerFactory.getLogger(OrganisationService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    /**
     * Create/Update new Organisation
     *
     * @param organisation
     * @return
     */
    @Transactional
    public Organisation save(Organisation organisation) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organisation newOrganisation = null;
        if (organisation.getId() == null) {
            organisation.setStatus(Status.ACTIVE);
            newOrganisation = organisationRepository.save(organisation);
            activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.ORGANISATION_ONBOARD));
        } else {
            newOrganisation = organisationRepository.save(organisation);
            activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.UPDATE_ORGANISATION));
        }
        return newOrganisation;
    }

    /**
     * Update Status
     *
     * @param status
     * @param organisationIds
     */
    @Transactional
    public void updateStatus(Status status, List<BigInteger> organisationIds) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        organisationRepository.updateStatus(status, organisationIds);
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.UPDATE_ORGANISATION));
    }

    /**
     * Get Organisation
     *
     * @param organisationId
     * @return
     */
    @Transactional(readOnly = true)
    public Organisation getOrganisationById(BigInteger organisationId) {
        Optional<Organisation> organisation = organisationRepository.findById(organisationId);
        if (organisation.isPresent()) {
            return organisation.get();
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * Upload Organisation image
     *
     * @param request
     * @return
     */
    @Transactional
    public OrganisationDTO uploadOrganisationLogo(HttpServletRequest request) {
        OrganisationDTO organisationDTO = new OrganisationDTO();
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new AppException(GenericErrorCode.FILE_UPLOAD_ISSUE);
        }
        MediaFile mediaFile = mediaService.getMediaFile(EntityType.ORGANISATION, MediaType.ORGANISATION_LOGO_IMAGE, loggedInUser.getOrganisationId());
        mediaFile = (mediaFile == null) ? new MediaFile() : mediaFile;
        mediaFile.setEntityId(loggedInUser.getOrganisationId());
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
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.ORGANISATION_LOGO_UPLOAD));
        return organisationDTO;
    }

    /**
     * Get all organisation
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Organisation> getOrganisations(String name, Status status, Pageable pageable) {
        name = (!name.equals("")) ? "%" + name.toUpperCase() + "%" : null;
        log.info("Name => {}, Status => {}", name, status);
        return new PaginatedList<Organisation>(organisationRepository.findAll(name, status.ordinal(), pageable));


        /*CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Organisation> criteriaQuery = criteriaBuilder.createQuery(Organisation.class);
        Root<Organisation> root = criteriaQuery.from(Organisation.class);
        criteriaQuery.select(root);
        List<Organisation> result = new ArrayList<Organisation>();
        if (name != null) {
            log.info("Name => {}", name);
            criteriaQuery.where(criteriaBuilder.like(root.get("name"), "%".concat(name).concat("%")));
        }
        if (status != Status.ALL) {
            log.info("Status => {}", status);
            criteriaQuery.where(criteriaBuilder.equal(root.get("status"), status));
        }
        result = entityManager
                .createQuery(criteriaQuery)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .getResultList();
        return new PaginatedList<Organisation>(new PageImpl<Organisation>(result, pageable, result.size()));*/
    }

    /**
     * Get organisation logo and settings
     *
     * @return
     */
    /*public OrganisationDTO getOrganisationSettings() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrganisationDTO organisationDTO = new OrganisationDTO();
        Organisation organisation = this.getOrganisationById(loggedInUser.getOrganisationId());
        organisationDTO.setMediaFile(mediaService.getMediaFile(EntityType.ORGANISATION, MediaType.ORGANISATION_LOGO_IMAGE, loggedInUser.getOrganisationId()));
        return organisationDTO;
    }*/

    /*public void updateOrganisationSettings(OrganisationDTO organisationDTO) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organisation organisation = redisService.getCachedOrganisation(loggedInUser.getOrganisationId());
        organisationDao.updateSetting(organisationDTO.getSetting(), loggedInUser.getOrganisationId());
        redisService.redisOperation(organisation.getId(), Utility.initializeAndUnproxy(organisation));
        activityLogService.save(new ActivityLog(loggedInUser.getUserId(), (loggedInUser.getOrganisationId() != null) ? loggedInUser.getOrganisationId() : null, ActivityType.UPDATE_ORGANISATION));
    }*/


}