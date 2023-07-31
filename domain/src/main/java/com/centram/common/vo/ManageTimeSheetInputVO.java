package com.centram.common.vo;


import com.centram.domain.Project;
import com.centram.domain.enumarator.BillingType;
import com.centram.domain.enumarator.TaskType;
import com.centram.domain.enumarator.TimeSheetLocationType;
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
    private List<TaskType> tasks;
    private List<TimeSheetLocationType> locations;
    private List<BillingType> billingTypes;

    public ManageTimeSheetInputVO(List<Project> projects) {
        this.projects = projects;
        this.tasks = Arrays.asList(TaskType.values());
        this.locations = Arrays.asList(TimeSheetLocationType.values());
        this.billingTypes = Arrays.asList(BillingType.values());
    }
}