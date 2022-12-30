package com.centram.common.vo;

import com.centram.domain.Department;
import com.centram.domain.enumarator.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Department
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DepartmentVO implements Serializable {
    private static final long serialVersionUID = 7423295630208486310L;
    private BigInteger id;
    private String name;
    private String status;

    public DepartmentVO(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.status = department.getStatus().toString();
    }
}