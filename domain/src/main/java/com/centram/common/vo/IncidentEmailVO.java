package com.centram.common.vo;

import com.centram.domain.Incident;
import com.centram.domain.Notification;
import com.centram.domain.enumarator.LicenseType;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IncidentEmailVO implements Serializable {
    private static final long serialVersionUID = -6554446908157662441L;
    private String mailSubjectKey;
    private String mailBodyKey;
    private String[] to;
    private String[] cc;
    private String[] bcc;
    private String recipientName;
    private String categoryAdminEmail;
    private String mailToType;
    private String replyTo;
    private BigInteger incidentId;
    private String incidentNo;
    private String title;
    private String description;
    private String priority;
    private String sla;
    private String status;
    private String category;
    private String subCategory;
    private String department;
    private BigInteger userManagerId;
    private BigInteger userId;
    private Long userVersion;
    private String userName;
    private String userEmail;
    private String userContactNo;
    private String userLocation;
    private String watchList;
    private BigInteger agentManagerId;
    private BigInteger agentId;
    private Long agentVersion;
    private String agentName = "NA";
    private String agentEmail = "NA";
    private String agentContactNo = "NA";
    private String escalation1Name;
    private String escalation1Email;
    private String escalation2Name;
    private String escalation2Email;
    private BigInteger moduleId;
    private BigInteger subModuleId;
    private BigInteger organisationId;
    private List<UserVO> userVOS;
    private Boolean newIncident = false;
    private Boolean reopened = false;
    private List<Notification> notifications;
    private LicenseType incidentType;
    private Boolean assetApproved;
    private Boolean feedbackProvided;
    private String serialNo;
    private String modelNo;
    private Boolean deallocated;

    public IncidentEmailVO(Incident incident, String dateTimeFormat, String customComment, Boolean reopened, Boolean deallocated) {
        ZonedDateTime sla = incident.getSlaAt().atZone(ZoneId.systemDefault());
        sla = sla.withZoneSameInstant(ZoneId.of(incident.getRaisedUser().getLocation().getTimezone()));
        this.reopened = reopened;
        this.deallocated = deallocated;
        this.watchList = incident.getWatchList() != null ? incident.getWatchList().stream().map(String::toString).collect(Collectors.joining(",")) : "";
        this.moduleId = incident.getModuleId();
        this.subModuleId = incident.getSubModuleId();
        this.mailToType = "USER";
        this.incidentId = incident.getId();
        this.incidentNo = incident.getIncidentNo();
        this.title = incident.getTitle();
        this.incidentType = incident.getIncidentType();
        this.assetApproved = incident.getAssetApproved();
        this.feedbackProvided = incident.getFeedbackProvided();
        this.serialNo = incident.getAsset() == null ? "" : incident.getAsset().getSerialNo();
        this.modelNo = incident.getAsset() == null ? "" : incident.getAsset().getModelNo();
        this.newIncident = incident.getCommunications().size() == 1;
        this.description = (customComment == null) ? incident.getCommunications().iterator().next().getMessage() : customComment;
        this.priority = incident.getPriority().getName();
        this.sla = sla.toLocalDateTime().format(DateTimeFormatter.ofPattern(dateTimeFormat));
        this.status = incident.getStatus().name();
        this.department = incident.getRaisedUser().getDepartment().getName();
        this.userManagerId = incident.getRaisedUser().getManagerId();
        this.userId = incident.getRaisedUser().getId();
        this.userVersion = incident.getRaisedUser().getVersion();
        this.userName = incident.getRaisedUser().getFirstName().concat(" ").concat(incident.getRaisedUser().getLastName());
        this.userEmail = incident.getRaisedUser().getEmail();
        this.userContactNo = incident.getRaisedUser().getContactNo();
        this.userLocation = incident.getRaisedUser().getLocation().getName();
        this.organisationId = incident.getRaisedUser().getOrganisation().getId();
        if (incident.getAssignedUser() != null) {
            this.agentId = incident.getAssignedUser().getId();
            this.agentVersion = incident.getAssignedUser().getVersion();
            this.agentManagerId = incident.getAssignedUser().getManagerId();
            this.agentName = incident.getAssignedUser().getFirstName().concat(" ").concat(incident.getAssignedUser().getLastName());
            this.agentEmail = incident.getAssignedUser().getEmail();
            this.agentContactNo = incident.getAssignedUser().getContactNo();
        }
        this.reopened = false;
    }

    public IncidentEmailVO(Incident incident, String dateTimeFormat, String customComment) {
        ZonedDateTime sla = incident.getSlaAt().atZone(ZoneId.systemDefault());
        sla = sla.withZoneSameInstant(ZoneId.of(incident.getRaisedUser().getLocation().getTimezone()));
        this.watchList = incident.getWatchList() != null ? incident.getWatchList().stream().map(String::toString).collect(Collectors.joining(",")) : "";
        this.moduleId = incident.getModuleId();
        this.subModuleId = incident.getSubModuleId();
        this.mailToType = "USER";
        this.incidentId = incident.getId();
        this.incidentNo = incident.getIncidentNo();
        this.title = incident.getTitle();
        this.incidentType = incident.getIncidentType();
        this.assetApproved = incident.getAssetApproved();
        this.feedbackProvided = incident.getFeedbackProvided();
        this.serialNo = incident.getAsset() == null ? "" : incident.getAsset().getSerialNo();
        this.modelNo = incident.getAsset() == null ? "" : incident.getAsset().getModelNo();
        this.newIncident = incident.getCommunications().size() == 1;
        this.description = (customComment == null) ? incident.getCommunications().iterator().next().getMessage() : customComment;
        this.priority = incident.getPriority().getName();
        this.sla = sla.toLocalDateTime().format(DateTimeFormatter.ofPattern(dateTimeFormat));
        this.status = incident.getStatus().name();
        this.department = incident.getRaisedUser().getDepartment().getName();
        this.userManagerId = incident.getRaisedUser().getManagerId();
        this.userId = incident.getRaisedUser().getId();
        this.userVersion = incident.getRaisedUser().getVersion();
        this.userName = incident.getRaisedUser().getFirstName().concat(" ").concat(incident.getRaisedUser().getLastName());
        this.userEmail = incident.getRaisedUser().getEmail();
        this.userContactNo = incident.getRaisedUser().getContactNo();
        this.userLocation = incident.getRaisedUser().getLocation().getName();
        this.organisationId = incident.getRaisedUser().getOrganisation().getId();
        if (incident.getAssignedUser() != null) {
            this.agentId = incident.getAssignedUser().getId();
            this.agentVersion = incident.getAssignedUser().getVersion();
            this.agentManagerId = incident.getAssignedUser().getManagerId();
            this.agentName = incident.getAssignedUser().getFirstName().concat(" ").concat(incident.getAssignedUser().getLastName());
            this.agentEmail = incident.getAssignedUser().getEmail();
            this.agentContactNo = incident.getAssignedUser().getContactNo();
        }
        this.reopened = false;
    }

    public IncidentEmailVO(Incident incident, String dateTimeFormat, String customComment, Boolean reopened) {
        ZonedDateTime sla = incident.getSlaAt().atZone(ZoneId.systemDefault());
        sla = sla.withZoneSameInstant(ZoneId.of(incident.getRaisedUser().getLocation().getTimezone()));
        this.reopened = reopened;
        this.watchList = incident.getWatchList() != null ? incident.getWatchList().stream().map(String::toString).collect(Collectors.joining(",")) : "";
        this.moduleId = incident.getModuleId();
        this.subModuleId = incident.getSubModuleId();
        this.mailToType = "USER";
        this.incidentId = incident.getId();
        this.incidentNo = incident.getIncidentNo();
        this.title = incident.getTitle();
        this.incidentType = incident.getIncidentType();
        this.assetApproved = incident.getAssetApproved();
        this.feedbackProvided = incident.getFeedbackProvided();
        this.serialNo = incident.getAsset() == null ? "" : incident.getAsset().getSerialNo();
        this.modelNo = incident.getAsset() == null ? "" : incident.getAsset().getModelNo();
        this.newIncident = incident.getCommunications().size() == 1;
        this.description = (customComment == null) ? incident.getCommunications().iterator().next().getMessage() : customComment;
        this.priority = incident.getPriority().getName();
        this.sla = sla.toLocalDateTime().format(DateTimeFormatter.ofPattern(dateTimeFormat));
        this.status = incident.getStatus().name();
        this.department = incident.getRaisedUser().getDepartment().getName();
        this.userManagerId = incident.getRaisedUser().getManagerId();
        this.userId = incident.getRaisedUser().getId();
        this.userVersion = incident.getRaisedUser().getVersion();
        this.userName = incident.getRaisedUser().getFirstName().concat(" ").concat(incident.getRaisedUser().getLastName());
        this.userEmail = incident.getRaisedUser().getEmail();
        this.userContactNo = incident.getRaisedUser().getContactNo();
        this.userLocation = incident.getRaisedUser().getLocation().getName();
        this.organisationId = incident.getRaisedUser().getOrganisation().getId();
        if (incident.getAssignedUser() != null) {
            this.agentId = incident.getAssignedUser().getId();
            this.agentVersion = incident.getAssignedUser().getVersion();
            this.agentManagerId = incident.getAssignedUser().getManagerId();
            this.agentName = incident.getAssignedUser().getFirstName().concat(" ").concat(incident.getAssignedUser().getLastName());
            this.agentEmail = incident.getAssignedUser().getEmail();
            this.agentContactNo = incident.getAssignedUser().getContactNo();
        }
    }

    public void populateEscalationMatrices() {
        List<UserVO> firstLevelEscalation = this.userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.equals("ORG_INCIDENT_AGENT_LEAD")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<UserVO> secondLevelEscalation = this.userVOS.stream()
                .filter(i -> {
                    if (i.getRoleNames().size() > 0) {
                        List<String> roles = i.getRoleNames();
                        for (String s : roles) {
                            if (s.equals("ORG_INCIDENT_AGENT_MANAGER")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
        for (UserVO userVO : firstLevelEscalation) {
            this.escalation1Name = userVO.getFullName().concat(",");
            this.escalation1Email = userVO.getEmail().concat(",");
        }
        for (UserVO userVO : secondLevelEscalation) {
            this.escalation2Name = userVO.getFullName().concat(",");
            this.escalation2Email = userVO.getEmail().concat(",");
        }
        this.escalation1Name = StringUtils.stripEnd(escalation1Name, ",");
        this.escalation1Email = StringUtils.stripEnd(escalation1Email, ",");
        this.escalation2Name = StringUtils.stripEnd(escalation2Name, ",");
        this.escalation2Email = StringUtils.stripEnd(escalation2Email, ",");
    }
}