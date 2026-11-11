package com.codealpha.jobboard.controller;

import com.codealpha.jobboard.dto.Dtos.NotificationResponse;
import com.codealpha.jobboard.security.CurrentUser;
import com.codealpha.jobboard.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;
    private final CurrentUser currentUser;

    @GetMapping
    public List<NotificationResponse> mine(Authentication auth) {
        return service.mine(currentUser.from(auth));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> read(@PathVariable Long id, Authentication auth) {
        service.markSeen(id, currentUser.from(auth));
        return ResponseEntity.noContent().build();
    }
}
