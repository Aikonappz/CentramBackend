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
 * Action
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "chat_room")
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = -7098397666742361288L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "chat_room_no")
    @JsonView(Views.BasicView.class)
    private String chatRoomNo;

    
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
    @Column(name = "session_start")
    @JsonView(Views.BasicView.class)
    private LocalDateTime sessionStart;

    
    @NotNull
    @Column(name = "session_end")
    @JsonView(Views.BasicView.class)
    private LocalDateTime sessionEnd;
}