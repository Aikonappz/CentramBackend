package com.centram.core.repository;


import com.centram.domain.Action;
import com.centram.domain.ChatMessage;
import com.centram.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, BigInteger> {

    @Query(value = "select cr from ChatRoom cr where cr.senderId = (:senderId) and " +
            "   ((:chatRoomNo) is not null and cr.chatRoomNo = (:chatRoomNo) ) " +
            "   OR " +
            "   ((:chatRoomNo) is null) "
    )
    Page<ChatRoom> findAll(@Param("chatRoomNo") String chatRoomNo, @Param("senderId") BigInteger senderId, Pageable pageable);
    
}