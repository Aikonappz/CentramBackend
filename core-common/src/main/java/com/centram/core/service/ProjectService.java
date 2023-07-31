package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ProjectAllocationDetailRepository;
import com.centram.core.repository.ProjectRepository;
import com.centram.domain.Project;
import com.centram.domain.enumarator.ProjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectAllocationDetailRepository projectAllocationDetailRepository;


    /**
     * get project
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Project getById(BigInteger id) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Project> optProject = projectRepository.findById(id);
        if (!optProject.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return optProject.get();
    }

    /**
     * get project by name
     *
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Project getByName(String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectRepository.getByName(name, loggedInUser.getOrganisationId());
    }

    /**
     * get project by name and type
     *
     * @param projectType
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Project getByNameAndType(ProjectType projectType, String name) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectRepository.getByNameAndType(projectType, name, loggedInUser.getOrganisationId());
    }

    /**
     * get all Project
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Project> getProjects(BigInteger organisationId, String inHouse, ProjectType projectType, Pageable pageable) {
        Boolean hasFilter = !inHouse.equalsIgnoreCase("");
        Boolean inHouseFilter = false;
        if (hasFilter && inHouse.equals("1")) {
            inHouseFilter = true;
        } else if (hasFilter && inHouse.equals("0")) {
            inHouseFilter = false;
        } else {
            hasFilter = false;
        }
        return new PaginatedList<Project>(projectRepository.getByOrganisation(hasFilter, inHouseFilter, projectType.ordinal(), organisationId, pageable));
    }

    /**
     * save project
     *
     * @param project
     * @return
     */
    @Transactional
    public Project save(BigInteger organisationId, Project project) {
        project.setOrganisation(organisationService.getOrganisationById(organisationId));
        return projectRepository.save(project);
    }
}