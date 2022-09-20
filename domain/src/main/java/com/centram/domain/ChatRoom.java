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
 * Action
 */
@ApiModel(description = "Chat Room")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "chat_room")
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = -7098397666742361288L;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = false, value = "")
    @NotNull
    @Column(name = "chat_room_no")
    @JsonView(Views.BasicView.class)
    private String chatRoomNo;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sender_id")
    @JsonView(Views.BasicView.class)
    private BigInteger senderId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "recipient_id")
    @JsonView(Views.BasicView.class)
    private BigInteger recipientId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sender_name")
    @JsonView(Views.BasicView.class)
    private String senderName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "recipient_name")
    @JsonView(Views.BasicView.class)
    private String recipientName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "session_start")
    @JsonView(Views.BasicView.class)
    private LocalDateTime sessionStart;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "session_end")
    @JsonView(Views.BasicView.class)
    private LocalDateTime sessionEnd;
}