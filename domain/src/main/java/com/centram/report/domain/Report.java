package com.centram.report.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.RoleConverter;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "report")
public class Report implements Serializable {
    private static final long serialVersionUID = -1033451209367452581L;


    @NotNull
    @Valid
    @JsonView(Views.BasicView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;


    @NotNull
    @Valid
    @Lob
    @Column(name = "roles", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    @JsonView(Views.BasicView.class)
    private List<BigInteger> roles;


    @NotNull
    @Valid
    @Column(name = "name", nullable = false)
    @JsonView(Views.BasicView.class)
    private String name;


    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;
}
