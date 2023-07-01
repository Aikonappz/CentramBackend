package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * VendorModule
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "vendor_module",
        indexes = {
                @Index(name = "vndr_id_indx", columnList = "vendor_id", unique = false),
        }
)
@Audited
public class VendorModule extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337189073432054L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private BigInteger id;

    
    @NotNull
    @Column(name = "module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    
    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    @JsonView({Views.InternalView.class,})
    private Vendor vendor;

    public VendorModule(@NotNull BigInteger id) {
        this.id = id;
    }

    public VendorModule(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}