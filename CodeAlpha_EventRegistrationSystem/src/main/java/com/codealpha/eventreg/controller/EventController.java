package com.codealpha.eventreg.controller;

import com.codealpha.eventreg.dto.Dtos.*;
import com.codealpha.eventreg.security.CurrentUser;
import com.codealpha.eventreg.service.EventService;
import com.codealpha.eventreg.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final CurrentUser currentUser;

    // ---------- events ----------
    @GetMapping("/events")
    public List<EventResponse> list(@RequestParam(required = false) String q) {
        return eventService.list(q);
    }

    @GetMapping("/events/{id}")
    public EventResponse get(@PathVariable Long id) {
        return eventService.get(id);
    }

    @PostMapping("/events")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.create(req, currentUser.from(auth)));
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest req, Authentication auth) {
        return eventService.update(id, req, currentUser.from(auth));
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        eventService.delete(id, currentUser.from(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{id}/registrations")
    @PreAuthorize("hasRole('ORGANIZER')")
    public List<AttendeeResponse> attendees(@PathVariable Long id, Authentication auth) {
        return eventService.attendees(id, currentUser.from(auth));
    }

    // ---------- registrations ----------
    @PostMapping("/events/{id}/register")
    public ResponseEntity<RegistrationResponse> register(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.register(id, currentUser.from(auth)));
    }

    @GetMapping("/registrations/my")
    public List<RegistrationResponse> myRegistrations(Authentication auth) {
        return registrationService.mine(currentUser.from(auth));
    }

    @DeleteMapping("/registrations/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication auth) {
        registrationService.cancel(id, currentUser.from(auth));
        return ResponseEntity.noContent().build();
    }
}
