package com.tfm.edcuaweb.repository;

import com.tfm.edcuaweb.model.Notification;
import com.tfm.edcuaweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndReadFalse(User user);
}
