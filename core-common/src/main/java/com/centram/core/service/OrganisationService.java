package com.centram.core.service;

import com.centram.common.dto.LoggedInUser;
import com.centram.common.dto.OrganisationDTO;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.utility.Utility;
import com.centram.core.repository.OrganisationRepository;
import com.centram.domain.MediaFile;
import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.enumarator.*;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganisationService {

    private static final Logger log = LoggerFactory.getLogger(OrganisationService.class);

    @Value("${app.default.outbound.asset.req.prefix}")
    public String outboundAssetReqPrefix;

    @Value("${app.default.inbound.asset.req.prefix}")
    public String inboundAssetReqPrefix;

    @Value("${app.default.asset.prefix}")
    public String appDefaultAssetPrefix;

    @Value("${app.default.incident.prefix}")
    public String appDefaultIncidentPrefix;

    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private MiscService miscService;

    @Autowired
    /*@Qualifier("appEntityManager")*/
    private EntityManager entityManager;

    @Transactional
    public Organisation saveViaBatch(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    /**
     * save Organisation
     *
     * @param organisation
     * @return
     */
    @Transactional(readOnly = false)
    public Organisation save(Organisation organisation) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organisation newOrganisation = null;
        organisation.setLicenseEnd(organisation.getLicenseEnd().toLocalDate().plusDays(1).atStartOfDay().minusSeconds(1));
        organisation.setLicenseStart(organisation.getLicenseStart().toLocalDate().atStartOfDay().plusSeconds(1));
        organisation.setTan(Base64.getEncoder().encodeToString(organisation.getTan().getBytes(StandardCharsets.UTF_8)));
        organisation.setPan(Base64.getEncoder().encodeToString(organisation.getPan().getBytes(StandardCharsets.UTF_8)));
        organisation.setGstin(Base64.getEncoder().encodeToString(organisation.getGstin().getBytes(StandardCharsets.UTF_8)));
        if (organisation.getId() == null) {
            organisation.setStatus(Status.ACTIVE);
            organisation.setSetting(new Setting(IncidentAllocationType.GENERIC));
            newOrganisation = organisationRepository.save(organisation);
            miscService.organisationUpdate(newOrganisation, true);
        } else {
            newOrganisation = organisationRepository.save(organisation);
            if (newOrganisation.getStatus() == Status.INACTIVE) {
                miscService.organisationUpdate(newOrganisation, false);
            }
        }
        return newOrganisation;
    }

    /**
     * Update Status
     *
     * @param status
     * @param organisationIds
     */
    @Transactional(readOnly = false)
    public void updateStatus(Status status, List<BigInteger> organisationIds) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        organisationRepository.updateStatus(status, LocalDateTime.now(), organisationIds);
    }

    private Organisation prepareView(Organisation org) {
        try {
            if (Utility.isBase64(org.getGstin()))
                org.setGstin(new String(Base64.getDecoder().decode(org.getGstin())));
            if (Utility.isBase64(org.getPan()))
                org.setPan(new String(Base64.getDecoder().decode(org.getPan())));
            if (Utility.isBase64(org.getTan()))
                org.setTan(new String(Base64.getDecoder().decode(org.getTan())));
            return org;
        } catch (IllegalArgumentException iae) {
            return org;
        }
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
            return this.prepareView(organisation.get());
        } else {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
    }

    /**
     * @param centramKey
     * @return
     */
    @Transactional(readOnly = true)
    public Organisation getOrganisationByApiUserAndPassword(String centramKey, String centramPass) {
        Organisation organisation = organisationRepository.getOrganisationByApiUserKeyAndUserPassword(centramKey, centramPass);
        if (organisation != null) {
            return this.prepareView(organisation);
        }
        return null;
    }

    /**
     * Upload Organisation image
     *
     * @param request
     * @return
     */
    @Transactional(readOnly = false)
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
        //organisationDTO.setMediaFile(mediaService.save(mediaFile));
        return organisationDTO;
    }

    /**
     * get active organisations
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Organisation> getActiveOrganisations() {
        List<Organisation> organisations = organisationRepository.findAll();
        organisations.stream()
                .forEach(i -> {
                    i = this.prepareView(i);
                });
        return organisations;
    }

    /**
     * Get all organisation
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Organisation> getOrganisations(String name, Status status, LicenseType licenseType, Pageable pageable) {
        name = (!name.equals("")) ? "%" + name.toUpperCase() + "%" : null;
        log.info("Name => {}, Status => {}", name, status);
        Page<Organisation> page = organisationRepository.findAll(name, status.ordinal(), licenseType.ordinal(), pageable);
        page.getContent().stream()
                .forEach(i -> {
                    i = this.prepareView(i);
                });
        return new PaginatedList<Organisation>(page);
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
    @Transactional(readOnly = true)
    public Setting getOrganisationSettings() {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organisation organisation = this.getOrganisationById(loggedInUser.getOrganisationId());
        if (organisation == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        if (organisation.getSetting().getAssetPrefix() == null || organisation.getSetting().getAssetPrefix().equalsIgnoreCase("")) {
            organisation.getSetting().setAssetPrefix(appDefaultAssetPrefix);
        }
        if (organisation.getSetting().getIncidentPrefix() == null || organisation.getSetting().getIncidentPrefix().equalsIgnoreCase("")) {
            organisation.getSetting().setIncidentPrefix(appDefaultIncidentPrefix);
        }
        if (organisation.getSetting().getInboundAssetRequestPrefix() == null || organisation.getSetting().getInboundAssetRequestPrefix().equalsIgnoreCase("")) {
            organisation.getSetting().setInboundAssetRequestPrefix(inboundAssetReqPrefix);
        }
        if (organisation.getSetting().getOutboundAssetRequestPrefix() == null || organisation.getSetting().getOutboundAssetRequestPrefix().equalsIgnoreCase("")) {
            organisation.getSetting().setOutboundAssetRequestPrefix(outboundAssetReqPrefix);
        }
        return organisation.getSetting();
    }

    /**
     * update Organization settings
     *
     * @param setting
     */
    @Transactional(readOnly = false)
    public Setting updateOrganisationSettings(Setting setting) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        organisationRepository.updateSetting(setting, LocalDateTime.now(), loggedInUser.getOrganisationId());
        return setting;
    }

    /**
     * get Organisations those has round robin settings
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Organisation> getRoundRobinOrganisations() {
        List<Organisation> organisations = organisationRepository.findAll();
        return organisations.stream()
                .filter(i -> {
                    i = this.prepareView(i);
                    return i.getSetting() != null && i.getSetting().getTicketAllocationType() == IncidentAllocationType.ROUND_ROBIN;
                })
                .collect(Collectors.toList());
    }
}