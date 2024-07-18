package com.centram.common.vo;


import com.centram.domain.TimeSheetEntry;
import com.centram.domain.enumarator.BillingType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TimeSheetReportVO implements Serializable {
    private static final long serialVersionUID = 672115467273154434L;
    private BigInteger timesheetId;
    private BigInteger timesheetEntryId;
    private BigInteger userId;
    private String userName;
    private String userEmail;
    private String userEmpId;
    private String projectCode;
    private String projectName;
    private String task;
    private String taskDescription;
    private String totalHours;
    private Boolean billable;
    private Boolean approved;
    private Boolean rejected;
    private BigInteger approverId;
    private String approverName;
    private String approverEmail;
    private String approverEmpId;
    private String approverComment;

    public TimeSheetReportVO(TimeSheetEntry i) {
        this.approved = i.getApproved();
        this.timesheetId = i.getTimeSheet().getId();
        this.timesheetEntryId = i.getId();
        this.userId = i.getTimeSheet().getUser().getId();
        this.userName = i.getTimeSheet().getUser().getFirstName() + " " + i.getTimeSheet().getUser().getLastName();
        this.userEmail = i.getTimeSheet().getUser().getEmail();
        this.userEmpId = i.getTimeSheet().getUser().getEmployeeId();
        this.projectCode = i.getProject().getCode();
        this.projectName = i.getProject().getName();
        this.task = i.getTask().name();
        this.taskDescription = i.getUserComment();
        //this.totalHours = LocalTime.of(0, 0);
        this.billable = i.getBillingType().equals(BillingType.BILLABLE);
        this.rejected = i.getRejected();
        this.approverId = i.getApprover().getId();
        this.approverName = i.getApprover().getFirstName() + " " + i.getTimeSheet().getUser().getLastName();
        this.approverEmail = i.getApprover().getEmail();
        this.approverEmpId = i.getApprover().getEmployeeId();
        this.approverComment = i.getApproverComment();
        Long temp = 0l;
        for (Map.Entry<LocalDate, LocalTime> entry : i.getTimeEntries().entrySet()) {
            if (entry.getValue() != null){
                temp += entry.getValue().toSecondOfDay();
            }

              //  this.totalHours = this.totalHours.plusHours(entry.getValue().getHour()).plusMinutes(entry.getValue().getMinute());
        }
        Long hours = temp / 3600;
        Long minutes = (temp % 3600) / 60;
        Long seconds = temp % 60;
        this.totalHours = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}