package com.codealpha.jobboard.repository;

import com.codealpha.jobboard.model.Notification;
import com.codealpha.jobboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop50ByRecipientOrderByCreatedAtDesc(User recipient);
}
