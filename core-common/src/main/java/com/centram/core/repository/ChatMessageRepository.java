package com.centram.core.repository;


import com.centram.domain.AssetOrder;
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
public interface ChatMessageRepository extends JpaRepository<ChatMessage, BigInteger> {
    @Query(value = "select cm from ChatMessage cm where cm.roomId = (:chatRoomId)")
    Page<ChatMessage> findAll(@Param("chatRoomId") String chatRoomId, Pageable pageable);

    @Query(value = "select cm from ChatMessage cm where cm.roomId = (:chatRoomId) and roomClosed = false order by id asc")
    Page<ChatMessage> findAllOpenChat(@Param("chatRoomId") String chatRoomId, Pageable pageable);
}