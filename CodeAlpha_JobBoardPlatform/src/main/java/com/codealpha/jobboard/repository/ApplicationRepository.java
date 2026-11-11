package com.codealpha.jobboard.repository;

import com.codealpha.jobboard.model.ApplicationStatus;
import com.codealpha.jobboard.model.Job;
import com.codealpha.jobboard.model.JobApplication;
import com.codealpha.jobboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobAndCandidate(Job job, User candidate);
    long countByJob(Job job);
    long countByStatus(ApplicationStatus status);
    List<JobApplication> findByCandidateOrderByAppliedAtDesc(User candidate);
    List<JobApplication> findByJobOrderByAppliedAtDesc(Job job);
}
