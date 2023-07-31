package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.MessageStatus;
import com.centram.domain.enumarator.SenderType;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "chat_message")
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 8070246456938375936L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "module_id")
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    
    @NotNull
    @Column(name = "sub_module_id")
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    
    @NotNull
    @Column(name = "room_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private String roomId;

    
    @NotNull
    @Column(name = "sender_id")
    @JsonView(Views.BasicView.class)
    private BigInteger senderId;

    
    @NotNull
    @Column(name = "recipient_id")
    @JsonView(Views.BasicView.class)
    private BigInteger recipientId;

    
    @NotNull
    @Column(name = "sender_name")
    @JsonView(Views.BasicView.class)
    private String senderName;

    
    @NotNull
    @Column(name = "recipient_name")
    @JsonView(Views.BasicView.class)
    private String recipientName;

    
    @NotNull
    @Column(name = "content", columnDefinition = "varchar(3000) default null")
    @JsonView(Views.BasicView.class)
    private String content;

    
    @NotNull
    @Column(name = "conversation_time")
    @JsonView(Views.BasicView.class)
    private LocalDateTime conversationTime;

    
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private MessageStatus status;

    
    @Valid
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    @Transient
    private List<MediaFile> attachments;

    
    @Column(name = "room_closed", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean roomClosed = false;

    
    @NotNull
    @Valid
    @Column(name = "sender_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private SenderType senderType;
}