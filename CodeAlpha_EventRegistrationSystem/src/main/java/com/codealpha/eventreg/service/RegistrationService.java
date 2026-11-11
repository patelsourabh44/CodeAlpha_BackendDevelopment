package com.codealpha.eventreg.service;

import com.codealpha.eventreg.dto.Dtos.RegistrationResponse;
import com.codealpha.eventreg.exception.ApiException;
import com.codealpha.eventreg.model.Event;
import com.codealpha.eventreg.model.Registration;
import com.codealpha.eventreg.model.User;
import com.codealpha.eventreg.repository.EventRepository;
import com.codealpha.eventreg.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final EventRepository events;
    private final RegistrationRepository registrations;

    /** Locks the event row so two people cannot take the last seat at the same time. */
    @Transactional
    public RegistrationResponse register(Long eventId, User user) {
        Event event = events.findByIdForUpdate(eventId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This event has already taken place");
        }
        if (registrations.existsByUserAndEvent(user, event)) {
            throw new ApiException(HttpStatus.CONFLICT, "You are already registered for this event");
        }
        if (registrations.countByEvent(event) >= event.getCapacity()) {
            throw new ApiException(HttpStatus.CONFLICT, "Sorry, this event is full");
        }
        Registration r = new Registration();
        r.setUser(user);
        r.setEvent(event);
        return toResponse(registrations.save(r));
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> mine(User user) {
        return registrations.findByUserOrderByRegisteredAtDesc(user).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public void cancel(Long registrationId, User user) {
        Registration r = registrations.findById(registrationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Registration not found"));
        if (!r.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only cancel your own registrations");
        }
        registrations.delete(r);
    }

    private RegistrationResponse toResponse(Registration r) {
        Event e = r.getEvent();
        return new RegistrationResponse(r.getId(), e.getId(), e.getTitle(), e.getEventDate(),
                e.getLocation(), r.getRegisteredAt());
    }
}
