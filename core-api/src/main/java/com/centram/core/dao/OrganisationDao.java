package com.centram.core.dao;

//import com.erp.api.client.IUsersApiClient;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.Utility;
import com.centram.core.repository.MediaFileRepository;
import com.centram.core.repository.OrganisationRepository;
import com.centram.domain.MediaFile;
import com.centram.domain.Organisation;
import com.centram.domain.Setting;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Transactional
@Component
public class OrganisationDao {

    private static final Logger log = LoggerFactory.getLogger(OrganisationDao.class);

    @Autowired
    private OrganisationRepository organisationRepository;

    /*@Autowired
    private IUsersApiClient usersApiClient;*/

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Transactional
    public Organisation save(Organisation organisation) {

        Organisation newOrganisation = organisationRepository.save(organisation);
        return newOrganisation;
    }

    @Transactional
    public Organisation update(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    @Transactional
    public void updateStatus(Status status, BigInteger organisationId) {
        organisationRepository.updateStatus(status, organisationId);
    }

    @Transactional
    public void updateSetting(Setting setting, BigInteger organisationId) {
        organisationRepository.updateSetting(setting, organisationId);
    }

    @Transactional(readOnly = true)
    public Organisation getOrganisationById(BigInteger organisationId) {
        Organisation organisation = organisationRepository.getOne(organisationId);
        if (organisation == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return organisation;
    }

    @Transactional(readOnly = true)
    public MediaFile getOrganisationLogo(BigInteger organisationId) {
        return mediaFileRepository.getMediaFile(EntityType.ORGANISATION, MediaType.ORGANISATION_LOGO_IMAGE, organisationId);
    }

    @Transactional(readOnly = true)
    public Organisation getOrganisationByMnemonic(String mnemonic) {
        Organisation organisation = null;//organisationRepository.findByMnemonic(mnemonic);
        if (organisation == null) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return organisation;
    }

    @Transactional(readOnly = true)
    public Page<Organisation> getOrganisations(Pageable pageable) {
        return organisationRepository.findAll(pageable);
    }

    /*@Async("asyncExecutor")
    private User createUser(User user) {
        ResponseEntity<User> userResponseEntity = usersApiClient.addUser(user);
        return userResponseEntity.getBody();
    }*/
}
