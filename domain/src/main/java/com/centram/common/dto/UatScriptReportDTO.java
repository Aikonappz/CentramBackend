package com.centram.common.dto;

import com.centram.domain.Project;
import com.centram.domain.ProjectUat;
import com.centram.domain.ProjectUatScript;
import com.centram.domain.ProjectUatScriptDetail;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UatScriptReportDTO implements Serializable {
    private static final long serialVersionUID = -1177123688724269476L;

    private BigInteger moduleId;
    private BigInteger subModuleId;
    private Integer no;
    private String projectName;
    private String projectCode;
    private String projectType;
    private String projectCustomer;
    private String projectManager;
    private String consultantResponsible;
    private String technology;
    private String module;
    private String subModule;
    private String testCaseId;
    private String testCaseDescription;
    private String status;
    private String currentlyWith;
    private Long age;

    public UatScriptReportDTO(ProjectUat projectUat, ProjectUatScript projectUatScript) {
        this.moduleId = projectUat.getModuleId();
        this.subModuleId = projectUat.getSubModuleId();
        this.no = null;
        this.projectName = projectUat.getProject().getName();
        this.projectCode = projectUat.getProject().getCode();
        this.projectType = projectUat.getProject().getProjectType().name();
        this.projectCustomer = String.join(",", projectUat.getProject().getStakeHolders());
        this.projectManager = String.join(",", projectUat.getProject().getWatchList());
        this.consultantResponsible = String.join(",", projectUat.getProject().getConsultants());
        this.technology = projectUat.getTechnology().name();
        this.testCaseId = projectUatScript.getTestCaseId();
        this.testCaseDescription = projectUatScript.getTestCaseDescription();
        if (projectUatScript.getUatComplete()) {
            this.status = "Completed";
        } else if (!projectUatScript.getUatComplete() && projectUatScript.getProjectUatScriptDetails().stream().noneMatch(i -> {
            return ((i.getPass() != null && i.getPass()) && (i.getRetestPass() != null && i.getRetestPass()));
        })) {
            this.status = "Not Started";
        } else if (!projectUatScript.getUatComplete() && projectUatScript.getProjectUatScriptDetails().stream().anyMatch(i -> {
            return ((i.getPass() != null && i.getPass()) || (i.getRetestPass() != null && i.getRetestPass()));
        })) {
            this.status = "In Progress";
        }else {
            this.status = "Unknown";
        }
        this.currentlyWith = this.projectCustomer;
        this.age = projectUat.getCreatedDate().until(LocalDateTime.now(), ChronoUnit.DAYS);
    }
}
