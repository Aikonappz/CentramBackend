package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.common.vo.NotificationVO;
import com.centram.core.repository.NotificationRepository;
import com.centram.domain.Notification;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${app.ws.broker.prefix}")
    private String appWsBrokerPrefix;

    /**
     * get notification by id
     *
     * @param notificationId
     * @return
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "departments", key = "#departmentId")
    public Notification getById(BigInteger notificationId) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (!notification.isPresent()) {
            throw new AppException(GenericErrorCode.DATA_NOT_FOUND);
        }
        return notification.get();
    }

    /**
     * get all notification for an user
     *
     * @param status
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedList<Notification> getNotifications(String searchValue, Status status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        searchValue = (searchValue.equalsIgnoreCase("")) ? null : "%" + searchValue.toUpperCase(Locale.ROOT) + "%";
        return new PaginatedList<Notification>(notificationRepository.getNotifications(loggedInUser.getUserId(), status.ordinal(), searchValue, pageable));
    }

    /**
     * get notification for an user by status
     *
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserAndStatus(Status status) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.getNotificationsByUserAndStatus(loggedInUser.getUserId(), status);
    }

    /**
     * update notification status
     *
     * @param ids
     * @param status
     */
    @Transactional
    public void updateNotificationStatus(List<BigInteger> ids, Status status) {
        notificationRepository.updateNotificationStatus(ids, status, LocalDateTime.now());
    }

    /**
     * save notification
     *
     * @param notification
     * @return
     */
    @Transactional
    public Notification save(Notification notification) {
        notification = notificationRepository.save(notification);
        this.pushNotifications(Collections.singletonList(notification));
        return notification;
    }

    /**
     * save multiple notifications
     *
     * @param notifications
     */
    @Transactional(readOnly = false)
    public void save(List<Notification> notifications) {
        notifications = notificationRepository.saveAll(notifications);
        this.pushNotifications(notifications);
    }

    /**
     * push notifications to web socket
     *
     * @param notifications
     */
    private void pushNotifications(List<Notification> notifications) {
        try {
            for (Notification notification : notifications) {
                simpMessagingTemplate.convertAndSend(
                        appWsBrokerPrefix.concat("/notification/").concat(String.valueOf(notification.getUser().getId())),
                        objectMapper.writeValueAsString(new NotificationVO(notification))
                );
            }
        } catch (JsonProcessingException e) {
            log.error("NOTIFICATION PUSH ISSUE {}", e.getOriginalMessage());
        }
    }
}
