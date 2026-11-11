package com.codealpha.eventreg.service;

import com.codealpha.eventreg.dto.Dtos.*;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository events;
    private final RegistrationRepository registrations;

    @Transactional(readOnly = true)
    public List<EventResponse> list(String q) {
        String kw = q == null ? "" : q.trim();
        return events.search(kw).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EventResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public EventResponse create(EventRequest req, User organizer) {
        Event e = new Event();
        apply(e, req);
        e.setOrganizer(organizer);
        return toResponse(events.save(e));
    }

    @Transactional
    public EventResponse update(Long id, EventRequest req, User organizer) {
        Event e = findOwned(id, organizer);
        if (req.capacity() < registrations.countByEvent(e)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Capacity cannot be lower than the number of current registrations");
        }
        apply(e, req);
        return toResponse(e);
    }

    @Transactional
    public void delete(Long id, User organizer) {
        Event e = findOwned(id, organizer);
        registrations.deleteByEvent(e);
        events.delete(e);
    }

    @Transactional(readOnly = true)
    public List<AttendeeResponse> attendees(Long id, User organizer) {
        Event e = findOwned(id, organizer);
        return registrations.findByEventOrderByRegisteredAtAsc(e).stream()
                .map(r -> new AttendeeResponse(r.getId(), r.getUser().getName(),
                        r.getUser().getEmail(), r.getRegisteredAt()))
                .toList();
    }

    public Event find(Long id) {
        return events.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    private Event findOwned(Long id, User organizer) {
        Event e = find(id);
        if (!e.getOrganizer().getId().equals(organizer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only manage your own events");
        }
        return e;
    }

    private void apply(Event e, EventRequest r) {
        e.setTitle(r.title().trim());
        e.setDescription(r.description());
        e.setLocation(r.location().trim());
        e.setEventDate(r.eventDate());
        e.setCapacity(r.capacity());
    }

    private EventResponse toResponse(Event e) {
        long count = registrations.countByEvent(e);
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getLocation(),
                e.getEventDate(), e.getCapacity(), count, Math.max(0, e.getCapacity() - count),
                e.getOrganizer().getName(), e.getOrganizer().getId());
    }
}
