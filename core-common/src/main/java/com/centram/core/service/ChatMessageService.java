package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.NotificationVO;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.ChatMessageRepository;
import com.centram.core.repository.ChatRoomRepository;
import com.centram.domain.*;
import com.centram.domain.enumarator.MessageStatus;
import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ChatMessageService {
    private static final Logger log = LoggerFactory.getLogger(ChatMessageService.class);

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MiscService miscService;

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        if (chatMessage.getRoomId() == null) {
            chatMessage.setRoomId(UUID.randomUUID().toString());
        }
        chatMessage.setConversationTime(LocalDateTime.now());
        UserVO sender = userService.getUserById(chatMessage.getSenderId());
        UserVO recipient = null;
        if (chatMessage.getRecipientId() != null) {
            recipient = userService.getUserById(chatMessage.getRecipientId());
            chatMessage.setRecipientName(recipient.getFullName());
        }
        chatMessage.setSenderName(sender.getFullName());
        chatMessage.setStatus(MessageStatus.DELIVERED);
        ChatMessage message = chatMessageRepository.save(chatMessage);
        this.notifyAgent(Arrays.asList(message.getModuleId(), message.getSubModuleId()), message.getRoomId());
        miscService.pushChats(Arrays.asList(message), recipient == null ? chatMessage.getSenderId() : recipient.getId());
        return message;
    }

    @Transactional
    public List<ChatMessage> chatAction(String chatRoomId) {
        Page<ChatMessage> chatMessagePage = chatMessageRepository.findAll(chatRoomId, Pageable.unpaged());
        chatMessagePage.getContent().stream()
                .forEach(i -> {
                    LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    UserVO recipient = userService.getUserById(loggedInUser.getUserId());
                    i.setRecipientId(recipient.getId());
                    i.setRecipientName(recipient.getFullName());
                    i.setStatus(MessageStatus.RECEIVED);
                    miscService.pushChats(Arrays.asList(i), recipient == null ? i.getSenderId() : recipient.getId());
                });
        return chatMessageRepository.saveAll(chatMessagePage.getContent());
    }

    @Transactional(readOnly = true)
    private void notifyAgent(List<BigInteger> modules, String roomId) {
        List<UserVO> userList = userService.getUsersByModuleAndAction(modules, "SOLVE");
        Notification notification = new Notification();
        List<AppConfiguration> appConfigurations = appConfigService.getAppConfigurations(Arrays.asList("CHAT_ACTION_NOTIFICATION"));
        AppConfiguration appConfiguration = appConfigurations.stream()
                .filter(ac -> ac.getConfigurationKey().equals("CHAT_ACTION_NOTIFICATION"))
                .findFirst().get();
        String title = appConfiguration.getConfigurationProperties().get("title").toString();
        String body = null;
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("com_id", roomId);

        body = templateEngine.process(appConfiguration.getConfigurationValue(), context);
        for (UserVO userVO : userList) {
            notification = new Notification();
            notification.setUser(new User(userVO));
            notification.setNotificationType(NotificationType.ACTIONABLE);
            notification.setStatus(Status.PUSHED);
            notification.setNotificationTitle(title);
            notification.setNotificationBody(body);
            //notification.setNotificationBody(new String(Base64.getEncoder().encode(appConfiguration.getConfigurationValue().getBytes(StandardCharsets.UTF_8))));
            notificationService.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public ChatMessage get(BigInteger id) {
        Optional<ChatMessage> chatMessage = chatMessageRepository.findById(id);
        if (chatMessage.isPresent()) {
            return chatMessage.get();
        }
        throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
    }

    /*@Transactional
    public PaginatedList<ChatMessage> findAll(BigInteger chatRoomId, Pageable pageable) {
        return new PaginatedList<ChatMessage>(chatMessageRepository.findAll(chatRoomId, pageable));
    }*/

}
