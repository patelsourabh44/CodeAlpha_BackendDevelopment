package com.codealpha.jobboard.controller;

import com.codealpha.jobboard.dto.Dtos.UserResponse;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.repository.UserRepository;
import com.codealpha.jobboard.security.CurrentUser;
import com.codealpha.jobboard.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final FileStorageService storage;
    private final UserRepository users;
    private final CurrentUser currentUser;

    /** Upload (or replace) the candidate's resume: multipart form field "file". */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public UserResponse upload(@RequestParam("file") MultipartFile file, Authentication auth) {
        User u = currentUser.from(auth);
        String stored = storage.store(file, u.getId());
        u.setResumeFile(stored);
        u.setResumeOriginalName(file.getOriginalFilename());
        return AuthController.toResponse(users.save(u));
    }
}
