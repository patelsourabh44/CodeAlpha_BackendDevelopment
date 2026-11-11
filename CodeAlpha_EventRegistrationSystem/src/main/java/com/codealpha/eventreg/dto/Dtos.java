package com.codealpha.eventreg.dto;

import com.codealpha.eventreg.model.Role;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDateTime;

public final class Dtos {
    private Dtos() {}

    public record RegisterRequest(@NotBlank String name,
                                  @NotBlank @Email String email,
                                  @NotBlank @Size(min = 6, message = "must be at least 6 characters") String password,
                                  Role role) {}

    public record UserResponse(Long id, String name, String email, Role role) {}

    public record EventRequest(@NotBlank String title,
                               String description,
                               @NotBlank String location,
                               @NotNull @Future LocalDateTime eventDate,
                               @Min(1) int capacity) {}

    public record EventResponse(Long id, String title, String description, String location,
                                LocalDateTime eventDate, int capacity, long registered,
                                long seatsLeft, String organizerName, Long organizerId) {}

    public record RegistrationResponse(Long id, Long eventId, String eventTitle,
                                       LocalDateTime eventDate, String location, Instant registeredAt) {}

    public record AttendeeResponse(Long registrationId, String name, String email, Instant registeredAt) {}
}
