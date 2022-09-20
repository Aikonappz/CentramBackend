package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.ActionRepository;
import com.centram.core.repository.ChatRoomRepository;
import com.centram.domain.Action;
import com.centram.domain.ChatRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class ChatRoomService {
    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    public ChatRoom get(BigInteger id) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);
        if (chatRoom.isPresent()) {
            return chatRoom.get();
        }
        throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public PaginatedList<ChatRoom> findAll(String chatRoomNo, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        chatRoomNo = chatRoomNo.equals("") ? null : chatRoomNo;
        return new PaginatedList<ChatRoom>(chatRoomRepository.findAll(chatRoomNo, loggedInUser.getUserId(), pageable));
    }

}
