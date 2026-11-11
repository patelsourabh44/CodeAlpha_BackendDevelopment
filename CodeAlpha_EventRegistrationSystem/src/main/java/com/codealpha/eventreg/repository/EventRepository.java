package com.codealpha.eventreg.repository;

import com.codealpha.eventreg.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e where lower(e.title) like lower(concat('%', :kw, '%')) " +
           "or lower(e.location) like lower(concat('%', :kw, '%')) order by e.eventDate asc")
    List<Event> search(@Param("kw") String kw);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
