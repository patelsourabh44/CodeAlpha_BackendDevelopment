package com.codealpha.jobboard.repository;

import com.codealpha.jobboard.model.Job;
import com.codealpha.jobboard.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    /** Filters use "" / 0 for "not set" so no nullable parameters are needed. */
    @Query("select j from Job j where j.active = true " +
           "and (lower(j.title) like lower(concat('%', :kw, '%')) " +
           "     or lower(j.company) like lower(concat('%', :kw, '%')) " +
           "     or lower(j.description) like lower(concat('%', :kw, '%'))) " +
           "and lower(j.location) like lower(concat('%', :loc, '%')) " +
           "and (:type = '' or j.jobType = :type) " +
           "and j.salary >= :minSalary " +
           "order by j.postedAt desc")
    List<Job> search(@Param("kw") String keyword, @Param("loc") String location,
                     @Param("type") String type, @Param("minSalary") long minSalary, Pageable pageable);

    List<Job> findByEmployerOrderByPostedAtDesc(User employer);
    long countByActive(boolean active);
}
