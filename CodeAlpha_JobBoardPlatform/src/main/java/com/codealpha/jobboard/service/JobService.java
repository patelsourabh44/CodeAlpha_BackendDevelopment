package com.codealpha.jobboard.service;

import com.codealpha.jobboard.dto.Dtos.JobRequest;
import com.codealpha.jobboard.dto.Dtos.JobResponse;
import com.codealpha.jobboard.exception.ApiException;
import com.codealpha.jobboard.model.Job;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.repository.ApplicationRepository;
import com.codealpha.jobboard.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobService {
    private static final Set<String> TYPES = Set.of("FULL_TIME", "PART_TIME", "INTERNSHIP", "CONTRACT");

    private final JobRepository jobs;
    private final ApplicationRepository applications;

    @Transactional(readOnly = true)
    public List<JobResponse> search(String keyword, String location, String type, long minSalary,
                                    int page, int size) {
        String t = type == null ? "" : type.trim().toUpperCase();
        return jobs.search(clean(keyword), clean(location), t, Math.max(0, minSalary),
                        PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50)))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public JobResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public JobResponse create(JobRequest req, User employer) {
        Job j = new Job();
        apply(j, req, employer);
        j.setEmployer(employer);
        return toResponse(jobs.save(j));
    }

    @Transactional
    public JobResponse update(Long id, JobRequest req, User employer) {
        Job j = findOwned(id, employer);
        apply(j, req, employer);
        return toResponse(j);
    }

    /** Closes the listing (soft delete) so existing applications are kept. */
    @Transactional
    public void close(Long id, User employer) {
        findOwned(id, employer).setActive(false);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> forEmployer(User employer) {
        return jobs.findByEmployerOrderByPostedAtDesc(employer).stream().map(this::toResponse).toList();
    }

    public Job find(Long id) {
        return jobs.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Job not found"));
    }

    public Job findOwned(Long id, User employer) {
        Job j = find(id);
        if (!j.getEmployer().getId().equals(employer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only manage your own jobs");
        }
        return j;
    }

    private void apply(Job j, JobRequest r, User employer) {
        String type = r.jobType().trim().toUpperCase();
        if (!TYPES.contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "jobType must be one of " + TYPES);
        }
        j.setTitle(r.title().trim());
        j.setDescription(r.description().trim());
        j.setLocation(r.location().trim());
        j.setJobType(type);
        j.setSalary(r.salary());
        String company = r.company() != null && !r.company().isBlank() ? r.company().trim()
                : (employer.getCompanyName() != null ? employer.getCompanyName() : employer.getName());
        j.setCompany(company);
    }

    private String clean(String s) {
        return s == null ? "" : s.trim();
    }

    private JobResponse toResponse(Job j) {
        return new JobResponse(j.getId(), j.getTitle(), j.getCompany(), j.getDescription(), j.getLocation(),
                j.getJobType(), j.getSalary(), j.getEmployer().getName(), j.getPostedAt(),
                j.isActive(), applications.countByJob(j));
    }
}
