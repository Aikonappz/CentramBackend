package com.centram.domain;



import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * User Permissions
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "permission",
        indexes = {
                @Index(name = "perm_role_idx", columnList = "role_id", unique = false),
        },
        uniqueConstraints = @UniqueConstraint(name = "role_module_action_constraint", columnNames = {"role_id", "module_id", "action_id"})
)
public class Permission implements Serializable {
    private static final long serialVersionUID = -2590688718125313577L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    
    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    
    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;

    
    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "action_id", referencedColumnName = "id")
    private Action action;

    public Permission(Role role, Module module, Action action) {
        this.role = role;
        this.module = module;
        this.action = action;
    }
}