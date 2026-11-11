package com.codealpha.eventreg.config;

import com.codealpha.eventreg.model.Event;
import com.codealpha.eventreg.model.Role;
import com.codealpha.eventreg.model.User;
import com.codealpha.eventreg.repository.EventRepository;
import com.codealpha.eventreg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Seeds a demo organizer and a few events on first start. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final EventRepository events;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.existsByEmail("admin@event.com")) return;
        User admin = new User();
        admin.setName("Demo Organizer");
        admin.setEmail("admin@event.com");
        admin.setPassword(encoder.encode("admin123"));
        admin.setRole(Role.ORGANIZER);
        users.save(admin);

        seed(admin, "Java Spring Boot Workshop", "Hands-on REST API workshop.", "Bhopal", 30, 40);
        seed(admin, "Android Dev Meetup", "Talks on Jetpack Compose and Kotlin.", "Jabalpur", 45, 25);
        seed(admin, "Hackathon 2026", "24-hour campus hackathon.", "Indore", 60, 100);
    }

    private void seed(User organizer, String title, String desc, String location, int daysAhead, int capacity) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription(desc);
        e.setLocation(location);
        e.setEventDate(LocalDateTime.now().plusDays(daysAhead).withHour(10).withMinute(0).withSecond(0).withNano(0));
        e.setCapacity(capacity);
        e.setOrganizer(organizer);
        events.save(e);
    }
}
