package com.codealpha.jobboard.service;

import com.codealpha.jobboard.dto.Dtos.NotificationResponse;
import com.codealpha.jobboard.exception.ApiException;
import com.codealpha.jobboard.model.Notification;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;

    @Transactional
    public void notify(User recipient, String message) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setMessage(message);
        repo.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> mine(User user) {
        return repo.findTop50ByRecipientOrderByCreatedAtDesc(user).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.isSeen(), n.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void markSeen(Long id, User user) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!n.getRecipient().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your notification");
        }
        n.setSeen(true);
    }
}
