package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.IncidentAllocationType;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Account
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "account", indexes = {@Index(name = "acc_no_org_id_indx", columnList = "account_no,organisation_id", unique = true), @Index(name = "acc_org_id_indx", columnList = "organisation_id", unique = false),})
@Audited
public class Account extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4374104635965387182L;


    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;


    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;


    @NotNull
    @Column(name = "account_no", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String accountNo;


    @NotNull
    @Column(name = "contact_name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactName;


    @NotNull
    @Column(name = "contact_email", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactEmail;


    @NotNull
    @Column(name = "contact_number", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactNumber;


    @NotNull
    @Column(name = "contact_address", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactAddress;


    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;


    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;


    @NotNull
    @Valid
    @Column(name = "account_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private LicenseType accountType;


    @NotNull
    @Valid
    @Column(name = "ticket_allocation_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private IncidentAllocationType ticketAllocationType;

    @NotNull
    @Column(name = "contract_hours", nullable = true, columnDefinition = "varchar(255) default null")
    @JsonView(Views.BasicView.class)
    private String contractHours;

    public Account(@NotNull BigInteger id) {
        this.id = id;
    }

    public Account(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}