package com.codealpha.jobboard.controller;

import com.codealpha.jobboard.dto.Dtos.*;
import com.codealpha.jobboard.security.CurrentUser;
import com.codealpha.jobboard.service.ApplicationService;
import com.codealpha.jobboard.service.JobService;
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
public class JobController {
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final CurrentUser currentUser;

    // ---------- public search ----------
    @GetMapping("/jobs")
    public List<JobResponse> search(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String location,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(defaultValue = "0") long minSalary,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return jobService.search(keyword, location, type, minSalary, page, size);
    }

    @GetMapping("/jobs/{id}")
    public JobResponse get(@PathVariable Long id) {
        return jobService.get(id);
    }

    // ---------- employer ----------
    @PostMapping("/jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(req, currentUser.from(auth)));
    }

    @PutMapping("/jobs/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody JobRequest req, Authentication auth) {
        return jobService.update(id, req, currentUser.from(auth));
    }

    @DeleteMapping("/jobs/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> close(@PathVariable Long id, Authentication auth) {
        jobService.close(id, currentUser.from(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employer/jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<JobResponse> myJobs(Authentication auth) {
        return jobService.forEmployer(currentUser.from(auth));
    }

    @GetMapping("/jobs/{id}/applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<ApplicationResponse> applicants(@PathVariable Long id, Authentication auth) {
        return applicationService.forJob(id, currentUser.from(auth));
    }

    // ---------- candidate ----------
    @PostMapping("/jobs/{id}/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long id,
                                                     @Valid @RequestBody(required = false) ApplyRequest req,
                                                     Authentication auth) {
        String cover = req == null ? null : req.coverLetter();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(id, cover, currentUser.from(auth)));
    }
}
