package com.centram.domain;

import com.centram.domain.enumarator.Status;


import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * User Roles
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "role",
        indexes = {
                @Index(name = "role_name_idx", columnList = "name", unique = true),
        }
)
public class Role implements Serializable {
    private static final long serialVersionUID = -6354417405904984865L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    
    @NotNull
    @Column(name = "display_name", columnDefinition = "varchar(255) not null")
    private String displayName;

    
    @NotNull
    @Column(name = "description", columnDefinition = "varchar(1000) not null")
    private String description;

    
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public Role(@NotNull BigInteger id) {
        this.id = id;
    }
}