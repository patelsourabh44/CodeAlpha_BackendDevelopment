package com.codealpha.jobboard.dto;

import com.codealpha.jobboard.model.ApplicationStatus;
import com.codealpha.jobboard.model.Role;
import jakarta.validation.constraints.*;

import java.time.Instant;

public final class Dtos {
    private Dtos() {}

    public record RegisterRequest(@NotBlank String name,
                                  @NotBlank @Email String email,
                                  @NotBlank @Size(min = 6, message = "must be at least 6 characters") String password,
                                  @NotNull Role role,
                                  String companyName) {}

    public record UserResponse(Long id, String name, String email, Role role,
                               String companyName, String resumeName) {}

    public record JobRequest(@NotBlank String title,
                             @NotBlank String description,
                             @NotBlank String location,
                             @NotBlank String jobType,
                             @Min(0) long salary,
                             String company) {}

    public record JobResponse(Long id, String title, String company, String description, String location,
                              String jobType, long salary, String employerName, Instant postedAt,
                              boolean active, long applicants) {}

    public record ApplyRequest(@Size(max = 2000) String coverLetter) {}

    public record StatusRequest(@NotNull ApplicationStatus status) {}

    public record ApplicationResponse(Long id, Long jobId, String jobTitle, String company,
                                      String candidateName, String candidateEmail, String coverLetter,
                                      ApplicationStatus status, Instant appliedAt, String resumeName) {}

    public record NotificationResponse(Long id, String message, boolean seen, Instant createdAt) {}
}
