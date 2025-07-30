package com.centram.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "notification_tracker",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_org_bu_div_dept_user",
                        columnNames = {
                                "organisation_id",
                                "business_unit_id",
                                "division_id",
                                "department_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_org_bu_div_dept", columnList = "organisation_id, business_unit_id, division_id, department_id")
        }
)
public class NotificationTracker extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7297378144430685972L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "organisation_id", nullable = false)
    private BigInteger organisationId;

    @Column(name = "business_unit_id", nullable = false)
    private BigInteger businessUnitId;

    @Column(name = "division_id", nullable = false)
    private BigInteger divisionId;

    @Column(name = "department_id", nullable = false)
    private BigInteger departmentId;

    @Column(name = "user_id", nullable = false)
    private BigInteger userId;
}