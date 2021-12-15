package com.centram.common.vo;

import com.centram.domain.Incident;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IncidentEmailVO implements Serializable {
    private static final long serialVersionUID = -6554446908157662441L;
    private String[] to;
    private String[] cc;
    private String[] bcc;
    private String replyTo;
    private String incidentNo;
    private String title;
    private String description;
    private String priority;
    private String sla;
    private String status;
    private String category;
    private String subCategory;
    private String department;
    private String userName;
    private String userEmail;
    private String userContactNo;
    private String userLocation;
    private String watchList;
    private String agentName = "NA";
    private String agentEmail = "NA";
    private String agentContactNo = "NA";
    private String escalation1Name;
    private String escalation1Email;
    private String escalation2Name;
    private String escalation2Email;

    public IncidentEmailVO(Map<String, Object> map) {
        Incident incident = (Incident) map.get("incident");
        String category = (String) map.get("category");
        String subCategory = (String) map.get("subCategory");
        List<UserVO> userVOS = (List<UserVO>) map.get("userVOS");
        String dateTimeFormat = (String) map.get("dateTimeFormat");

        this.to = (String[]) map.get("to");
        this.cc = (String[]) map.get("cc");
        this.bcc = (String[]) map.get("bcc");
        this.replyTo = (String) map.get("replyTo");
        this.incidentNo = incident.getIncidentNo();
        this.title = incident.getTitle();
        this.description = incident.getCommunications().iterator().next().getMessage();
        this.watchList = incident.getWatchList().stream().map(String::toString).collect(Collectors.joining(","));
        this.priority = incident.getPriority().getName();
        this.sla = incident.getSlaAt().format(DateTimeFormatter.ofPattern(dateTimeFormat));
        this.status = incident.getStatus().name();
        this.category = category;
        this.subCategory = subCategory;
        this.department = incident.getRaisedUser().getDepartment().getName();
        this.userName = incident.getRaisedUser().getFirstName().concat(" ").concat(incident.getRaisedUser().getLastName());
        this.userEmail = incident.getRaisedUser().getEmail();
        this.userContactNo = incident.getRaisedUser().getContactNo();
        this.userLocation = incident.getRaisedUser().getLocation().getName();
        if (incident.getAssignedUser() != null) {
            this.agentName = incident.getAssignedUser().getFirstName().concat(" ").concat(incident.getRaisedUser().getLastName());
            this.agentEmail = incident.getAssignedUser().getEmail();
            this.agentContactNo = incident.getAssignedUser().getContactNo();
        }
        List<UserVO> firstLevelEscalation = userVOS.stream()
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
        List<UserVO> secondLevelEscalation = userVOS.stream()
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