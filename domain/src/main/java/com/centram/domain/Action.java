package com.centram.domain;



import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Action
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "action", indexes = {@Index(name = "role_name_idx", columnList = "name", unique = true)})
public class Action implements Serializable {
    private static final long serialVersionUID = -1033450309367452581L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(50) not null")
    private String name;
}