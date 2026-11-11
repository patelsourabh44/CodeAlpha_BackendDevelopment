package com.codealpha.jobboard.controller;

import com.codealpha.jobboard.dto.Dtos.UserResponse;
import com.codealpha.jobboard.model.ApplicationStatus;
import com.codealpha.jobboard.model.Role;
import com.codealpha.jobboard.repository.ApplicationRepository;
import com.codealpha.jobboard.repository.JobRepository;
import com.codealpha.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reporting + user management for administrators. */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository users;
    private final JobRepository jobs;
    private final ApplicationRepository applications;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("employers", users.countByRole(Role.EMPLOYER));
        out.put("candidates", users.countByRole(Role.CANDIDATE));
        out.put("activeJobs", jobs.countByActive(true));
        out.put("closedJobs", jobs.countByActive(false));
        out.put("totalApplications", applications.count());
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ApplicationStatus s : ApplicationStatus.values()) {
            byStatus.put(s.name(), applications.countByStatus(s));
        }
        out.put("applicationsByStatus", byStatus);
        return out;
    }

    @GetMapping("/users")
    public List<UserResponse> allUsers() {
        return users.findAll().stream().map(AuthController::toResponse).toList();
    }
}
