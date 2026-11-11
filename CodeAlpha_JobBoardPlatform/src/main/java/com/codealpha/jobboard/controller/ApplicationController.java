package com.codealpha.jobboard.controller;

import com.codealpha.jobboard.dto.Dtos.ApplicationResponse;
import com.codealpha.jobboard.dto.Dtos.StatusRequest;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.security.CurrentUser;
import com.codealpha.jobboard.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService service;
    private final CurrentUser currentUser;

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public List<ApplicationResponse> mine(Authentication auth) {
        return service.mine(currentUser.from(auth));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApplicationResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req,
                                            Authentication auth) {
        return service.updateStatus(id, req.status(), currentUser.from(auth));
    }

    @GetMapping("/{id}/resume")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Resource> resume(@PathVariable Long id, Authentication auth) {
        User employer = currentUser.from(auth);
        Resource file = service.resumeFor(id, employer);
        String name = service.resumeNameFor(id, employer);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(name == null ? "resume" : name).build().toString())
                .body(file);
    }
}
