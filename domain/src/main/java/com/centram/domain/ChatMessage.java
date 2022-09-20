package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.MessageStatus;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Action
 */
@ApiModel(description = "Chat Message")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "chat_message")
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 8070246456938375936L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "room_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private String roomId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sender_id")
    @JsonView(Views.BasicView.class)
    private BigInteger senderId;

    @ApiModelProperty(required = false, value = "")
    @NotNull
    @Column(name = "recipient_id")
    @JsonView(Views.BasicView.class)
    private BigInteger recipientId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sender_name")
    @JsonView(Views.BasicView.class)
    private String senderName;

    @ApiModelProperty(required = false, value = "")
    @NotNull
    @Column(name = "recipient_name")
    @JsonView(Views.BasicView.class)
    private String recipientName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "content")
    @JsonView(Views.BasicView.class)
    private String content;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "conversation_time")
    @JsonView(Views.BasicView.class)
    private LocalDateTime conversationTime;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private MessageStatus status;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    @Transient
    private List<MediaFile> attachments;
}