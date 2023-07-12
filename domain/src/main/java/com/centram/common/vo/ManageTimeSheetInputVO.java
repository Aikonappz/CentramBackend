package com.centram.common.vo;


import com.centram.domain.Project;
import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ManageTimeSheetInputVO implements Serializable {
    private static final long serialVersionUID = -6554446568157662441L;
    private List<Project> projects;
    private List<String> tasks;
    private List<String> locations;
    private List<String> billingTypes;

    public ManageTimeSheetInputVO(List<Project> projects) {
        this.projects = projects;
        this.tasks = Arrays.asList("Travel","Training","Req. Analysis & Backlog Groom","Architecture & Design","Configuration or Build","Migration","QA Testing","User Acceptance Testing","Release & Deployment","Warranty Support & HyperCare","Sprint Plng Prj & Srv Del Mgmt","Transition","Project Training & KEDB Act.","Review & Rework","Cont. Improvement & Automation","BCP");
        this.locations = Arrays.asList("ON","OFF");
        this.billingTypes = Arrays.asList("Billable","Non Billable");
    }
}