package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.IncidentRepository;
import com.centram.domain.*;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.centram.common.utility.Utility.incidentNo;

@Service
public class IncidentService {
    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);
    @Value("${app.default.incident.prefix}")
    public String appDefaultIncidentPrefix;
    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private OrganisationService organisationService;

    @Transactional(readOnly = true)
    public PaginatedList<Incident> getIncomingIncidents(String incidentNo, String moduleId, String subModuleId, String priorityId, String assignedUserId, String title, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = loggedInUser.getAuthorities().stream()
                .map(i -> i.getAuthority())
                .collect(Collectors.toList());
        List<Permission> permissions = permissionService.getPermissionByRoleNames(roles);
        List<BigInteger> modSubModIds = permissions.stream()
                .filter(i -> !i.getModule().getAppModule())
                .map(i -> i.getModule().getId())
                .collect(Collectors.toList());
        BigInteger uId = (!assignedUserId.equals("")) ? BigInteger.valueOf(Long.valueOf(assignedUserId)) : null;
        BigInteger pId = (!priorityId.equals("")) ? BigInteger.valueOf(Long.valueOf(priorityId)) : null;
        BigInteger mId = (!moduleId.equals("")) ? BigInteger.valueOf(Long.valueOf(moduleId)) : null;
        BigInteger smId = (!subModuleId.equals("")) ? BigInteger.valueOf(Long.valueOf(subModuleId)) : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncomingIncidents(
                incidentNo, mId, smId, pId, uId, modSubModIds, title, intStatus, pageable
        ));
    }

    @Transactional(readOnly = true)
    public PaginatedList<Incident> getIncidents(String incidentNo, String title, String status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        incidentNo = (!incidentNo.equals("")) ? "%" + incidentNo.toUpperCase() + "%" : null;
        title = (!title.equals("")) ? "%" + title.toUpperCase() + "%" : null;
        int intStatus = (!status.equals("")) ? IncidentStatus.valueOf(status).ordinal() : IncidentStatus.ALL.ordinal();
        return new PaginatedList<Incident>(incidentRepository.getIncidents(loggedInUser.getUserId(), incidentNo, title, intStatus, pageable));
    }

    @Transactional(readOnly = true)
    public Incident getIncidentById(BigInteger incidentId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Incident> incident = incidentRepository.findById(incidentId);
        if (!incident.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        Set<IncidentCommunication> communicationSet = new HashSet<IncidentCommunication>();
        for (IncidentCommunication incidentCommunication : incident.get().getCommunications()) {
            incidentCommunication.setAttachments(mediaService.getMediaFiles(incidentCommunication.getId(), EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION));
            communicationSet.add(incidentCommunication);
        }
        incident.get().setCommunications(communicationSet);
        return incident.get();
    }

    @Transactional(readOnly = false)
    public void assignIncidents(List<BigInteger> ids, BigInteger userId) {
        incidentRepository.assignIncidents(userId, LocalDateTime.now(), ids);
    }

    @Transactional(readOnly = false)
    public void changeStatus(String status, List<BigInteger> ids) {
        incidentRepository.changeStatus(IncidentStatus.valueOf(status), LocalDateTime.now(), ids);
    }

    @Transactional(readOnly = false)
    public Incident save(Incident incident) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO userVO = userService.getUserById(loggedInUser.getUserId());
        if (incident.getId() == null) {
            incident.setRaisedUser(new User(userVO.getVersion(), userVO.getId()));
            incident.setRaisedAt(LocalDateTime.now());
            Setting setting = organisationService.getOrganisationSettings();
            String prefix = (setting != null && setting.getIncidentPrefix() != null) ? setting.getIncidentPrefix() : appDefaultIncidentPrefix;
            incident.setIncidentNo(incidentNo(prefix));
        }
        Set<IncidentCommunication> communicationSet = new HashSet<IncidentCommunication>();
        for (IncidentCommunication incidentCommunication : incident.getCommunications()) {
            if (incidentCommunication.getId() == null) {
                incidentCommunication.setCommunicatedBy(new User(userVO.getVersion(), userVO.getId()));
            }
            incidentCommunication.setIncident(incident);
            communicationSet.add(incidentCommunication);
        }
        incident.setCommunications(communicationSet);
        return incidentRepository.save(incident);
    }
}
