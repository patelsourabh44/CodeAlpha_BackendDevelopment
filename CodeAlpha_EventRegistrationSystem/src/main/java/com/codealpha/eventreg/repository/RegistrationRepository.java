package com.codealpha.eventreg.repository;

import com.codealpha.eventreg.model.Event;
import com.codealpha.eventreg.model.Registration;
import com.codealpha.eventreg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    long countByEvent(Event event);
    boolean existsByUserAndEvent(User user, Event event);
    List<Registration> findByUserOrderByRegisteredAtDesc(User user);
    List<Registration> findByEventOrderByRegisteredAtAsc(Event event);
    void deleteByEvent(Event event);
}
