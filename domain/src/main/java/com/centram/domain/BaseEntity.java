package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 539663081459952070L;

    @Column(name = "created_date", nullable = true, updatable = false)
    @CreatedDate
    @JsonView(Views.BasicView.class)
    private LocalDateTime createdDate;

    @Column(name = "modified_date", nullable = true)
    @LastModifiedDate
    @JsonView(Views.BasicView.class)
    private LocalDateTime modifiedDate;

    @Column(name = "version", nullable = true)
    @Version
    @JsonView(Views.BasicView.class)
    private Long version;

    @Column(name = "modified_by", nullable = true)
    @LastModifiedBy
    @JsonView(Views.BasicView.class)
    private BigInteger modifiedBy;

    @Column(name = "created_by", nullable = true, updatable = false)
    @CreatedBy
    @JsonView(Views.BasicView.class)
    private BigInteger createdBy;

    public BaseEntity(Long version) {
        this.version = version;
    }
}
