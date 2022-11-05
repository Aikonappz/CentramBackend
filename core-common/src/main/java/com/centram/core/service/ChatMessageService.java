package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.vo.UserVO;
import com.centram.core.repository.ChatMessageRepository;
import com.centram.domain.AppConfiguration;
import com.centram.domain.ChatMessage;
import com.centram.domain.Notification;
import com.centram.domain.User;
import com.centram.domain.enumarator.MessageStatus;
import com.centram.domain.enumarator.NotificationType;
import com.centram.domain.enumarator.SenderType;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private UserAuthService userAuthService;

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        chatMessage.setConversationTime(LocalDateTime.now());
        UserVO sender = userService.getUserById(chatMessage.getSenderId());
        UserVO recipient = null;
        if (chatMessage.getRoomId() == null) {
            chatMessage.setSenderType(SenderType.USER);
            List<UserVO> userVOS = userService.getUsersByModuleAndAction(
                    Arrays.asList(chatMessage.getModuleId(), chatMessage.getSubModuleId()),
                    "SOLVE"
            );
            if (userAuthService.anyUserOnline(
                    userVOS.stream()
                            .filter(i -> {
                                return i.getId().compareTo(loggedInUser.getUserId()) != 0;
                            })
                            .map(UserVO::getId).collect(Collectors.toList())
            ) < 1) {
                throw new AppException(GenericErrorCode.AGENT_NOT_AVAILABLE);
            }
            chatMessage.setRoomId(UUID.randomUUID().toString());
        } else {
            Page<ChatMessage> chatMessages = chatMessageRepository.findAll(
                    chatMessage.getRoomId(),
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"))
            );
            if (chatMessages.getContent().size() > 0) {
                ChatMessage firstMessage = chatMessages.getContent().get(0);
                if (firstMessage.getRoomId() != null) {
                    chatMessage.setModuleId(firstMessage.getModuleId());
                    chatMessage.setSubModuleId(firstMessage.getSubModuleId());
                    if (chatMessage.getSenderId().compareTo(firstMessage.getSenderId()) == 0) {
                        chatMessage.setRecipientId(firstMessage.getRecipientId());
                        chatMessage.setSenderType(SenderType.USER);
                    } else if (chatMessage.getSenderId().compareTo(firstMessage.getRecipientId()) == 0) {
                        chatMessage.setRecipientId(firstMessage.getSenderId());
                        chatMessage.setSenderType(SenderType.AGENT);
                    }
                } else {
                    throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
                }
            }
        }
        if (chatMessage.getRecipientId() != null) {
            recipient = userService.getUserById(chatMessage.getRecipientId());
            chatMessage.setRecipientName(recipient.getFullName());
        }
        chatMessage.setSenderName(sender.getFullName());
        chatMessage.setStatus(MessageStatus.DELIVERED);
        ChatMessage message = chatMessageRepository.save(chatMessage);
        this.notifyAgent(Arrays.asList(message.getModuleId(), message.getSubModuleId()), message.getRoomId());
        miscService.pushChats(Arrays.asList(message), recipient == null ? Boolean.TRUE : Boolean.FALSE);
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
                    miscService.pushChats(Arrays.asList(i), Boolean.TRUE);
                });
        return chatMessageRepository.saveAll(chatMessagePage.getContent());
    }

    @Transactional
    public List<ChatMessage> chatClose(String chatRoomId) {
        Page<ChatMessage> chatMessagePage = chatMessageRepository.findAllOpenChat(chatRoomId, Pageable.unpaged());
        if (chatMessagePage.getContent().size() > 0) {
            chatMessagePage.getContent().stream()
                    .forEach(i -> {
                                i.setRoomClosed(true);
                            }
                    );
            miscService.sendChatInteractionEmail(chatMessagePage.getContent());
            return chatMessageRepository.saveAll(chatMessagePage.getContent());
        } else {
            return new ArrayList<ChatMessage>();
        }
    }

    @Transactional(readOnly = true)
    private void notifyAgent(List<BigInteger> modules, String roomId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
            if (userVO.getId().compareTo(loggedInUser.getUserId()) != 0) {
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
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> chatMassages(String chatRoomId) {
        Page<ChatMessage> chatMessagePage = chatMessageRepository.findAll(chatRoomId, Pageable.unpaged());
        return chatMessagePage.getContent();
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
