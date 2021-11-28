package com.centram.core.service;


import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.NotificationRepository;
import com.centram.domain.Notification;
import com.centram.domain.enumarator.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

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

    @Transactional(readOnly = true)
    public PaginatedList<Notification> getNotifications(BigInteger lastFetched, Status status, Pageable pageable) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        lastFetched = lastFetched == null ? BigInteger.ZERO : lastFetched;
        return new PaginatedList<Notification>(notificationRepository.getNotifications(loggedInUser.getUserId(), lastFetched, status.ordinal(), pageable));
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserAndStatus(Status status) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.getNotificationsByUserAndStatus(loggedInUser.getUserId(), status);
    }

    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional
    public void save(List<Notification> notifications) {
        notificationRepository.saveAll(notifications);
    }
}
