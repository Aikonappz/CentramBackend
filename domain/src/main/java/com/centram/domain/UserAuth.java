package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * User Auth
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(
        name = "user_auth",
        indexes = {
                @Index(name = "user_idx", columnList = "user_id", unique = false),
        }
)
public class UserAuth implements Serializable {
    private static final long serialVersionUID = 7161521276698505219L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "user_id", columnDefinition = "BIGINT")
    @JsonView(Views.BasicView.class)
    private BigInteger userId;

    
    @Column(name = "sign_in_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime signInAt;

    
    @Column(name = "sign_out_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime signOutAt;

    public UserAuth(BigInteger userId, LocalDateTime signInAt, LocalDateTime signOutAt) {
        this.userId = userId;
        this.signInAt = signInAt;
        this.signOutAt = signOutAt;
    }
}