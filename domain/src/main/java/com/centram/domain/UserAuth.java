package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "User Auth")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
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

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "user_id", columnDefinition = "BIGINT")
    @JsonView(Views.BasicView.class)
    private BigInteger userId;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "sign_in_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime signInAt;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "sign_out_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime signOutAt;

    public UserAuth(BigInteger userId, LocalDateTime signInAt, LocalDateTime signOutAt) {
        this.userId = userId;
        this.signInAt = signInAt;
        this.signOutAt = signOutAt;
    }
}