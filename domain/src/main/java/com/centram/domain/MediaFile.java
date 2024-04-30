package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.EntityType;
import com.centram.domain.enumarator.MediaType;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "media_file",
        indexes = {
                @Index(name = "media_srch_idx", columnList = "entity_type,media_type,entity_id", unique = false)
        }
)
public class MediaFile implements Serializable {
    private static final long serialVersionUID = 5381555771178250038L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private BigInteger id;

    
    @Column(name = "file_name", nullable = false)
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private String fileName;

    
    @Column(name = "file_type", nullable = false)
    @JsonView({Views.InternalView.class})
    private String fileType;

    
    @Column(name = "entity_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView({Views.InternalView.class})
    private EntityType entityType;

    
    @Column(name = "media_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView({Views.InternalView.class})
    private MediaType mediaType;

    @Lob
    @Column(name = "content", nullable = false)
    @JsonView({Views.InternalView.class})
    private byte[] content;

    
    @Column(name = "entity_id", columnDefinition = "BIGINT")
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private BigInteger entityId;

    
    @Column(name = "chat_room_id")
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private String chatRoomId;

    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User user;

    @NotNull
    @Column(name = "uploaded_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime uploadedAt = LocalDateTime.now();

}