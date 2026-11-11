package com.codealpha.jobboard.service;

import com.codealpha.jobboard.dto.Dtos.ApplicationResponse;
import com.codealpha.jobboard.exception.ApiException;
import com.codealpha.jobboard.model.*;
import com.codealpha.jobboard.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applications;
    private final JobService jobService;
    private final NotificationService notifications;
    private final FileStorageService storage;

    @Transactional
    public ApplicationResponse apply(Long jobId, String coverLetter, User candidate) {
        Job job = jobService.find(jobId);
        if (!job.isActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This job is no longer accepting applications");
        }
        if (candidate.getResumeFile() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please upload your resume before applying");
        }
        if (applications.existsByJobAndCandidate(job, candidate)) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already applied for this job");
        }
        JobApplication a = new JobApplication();
        a.setJob(job);
        a.setCandidate(candidate);
        a.setCoverLetter(coverLetter);
        a.setResumeFile(candidate.getResumeFile());
        a.setResumeOriginalName(candidate.getResumeOriginalName());
        a = applications.save(a);
        notifications.notify(job.getEmployer(),
                candidate.getName() + " applied for \"" + job.getTitle() + "\"");
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> mine(User candidate) {
        return applications.findByCandidateOrderByAppliedAtDesc(candidate).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> forJob(Long jobId, User employer) {
        Job job = jobService.findOwned(jobId, employer);
        return applications.findByJobOrderByAppliedAtDesc(job).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus status, User employer) {
        JobApplication a = findForEmployer(id, employer);
        a.setStatus(status);
        notifications.notify(a.getCandidate(), "Your application for \"" + a.getJob().getTitle()
                + "\" at " + a.getJob().getCompany() + " is now: " + status.name().replace('_', ' '));
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public Resource resumeFor(Long id, User employer) {
        JobApplication a = findForEmployer(id, employer);
        return storage.load(a.getResumeFile());
    }

    @Transactional(readOnly = true)
    public String resumeNameFor(Long id, User employer) {
        return findForEmployer(id, employer).getResumeOriginalName();
    }

    private JobApplication findForEmployer(Long id, User employer) {
        JobApplication a = applications.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Application not found"));
        if (!a.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your job posting");
        }
        return a;
    }

    private ApplicationResponse toResponse(JobApplication a) {
        return new ApplicationResponse(a.getId(), a.getJob().getId(), a.getJob().getTitle(),
                a.getJob().getCompany(), a.getCandidate().getName(), a.getCandidate().getEmail(),
                a.getCoverLetter(), a.getStatus(), a.getAppliedAt(), a.getResumeOriginalName());
    }
}
