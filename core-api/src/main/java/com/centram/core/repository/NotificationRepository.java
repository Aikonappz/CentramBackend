package com.centram.core.repository;


import com.centram.domain.Notification;
import com.centram.domain.enumarator.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, BigInteger> {
    @Query("select n from Notification n where n.user.id = (:idUser) and n.status = (:status)")
    List<Notification> getNotificationsByUserAndStatus(@Param("idUser") BigInteger idUser, @Param("status") Status status);
}