package com.centram.core.repository;


import com.centram.domain.Notification;
import com.centram.domain.enumarator.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, BigInteger> {
    @Query("select n from Notification n where n.user.id = (:idUser) and n.status = (:status)")
    List<Notification> getNotificationsByUserAndStatus(@Param("idUser") BigInteger idUser, @Param("status") Status status);

    @Query("select n from Notification n where n.user.id = (:id) and " +
            " ( " +
            "   ((:status) <> 2 and n.status = (:status)) " +
            "   OR " +
            "   ((:status) = 2) " +
            " ) "
    )
    Page getNotifications(@Param("id") BigInteger id, @Param("status") Integer status, Pageable pageable);


    @Modifying
    @Query("update Notification set status = (:status), modifiedDate = (:modifiedAt) where id in (:ids)")
    Integer updateNotificationStatus(@Param("ids") List<BigInteger> ids, @Param("status") Status status, @Param("modifiedAt") LocalDateTime modifiedAt);
}