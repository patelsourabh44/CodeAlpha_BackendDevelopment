package com.codealpha.jobboard.config;

import com.codealpha.jobboard.model.Job;
import com.codealpha.jobboard.model.Role;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.repository.JobRepository;
import com.codealpha.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Seeds demo accounts and sample jobs on first start. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final JobRepository jobs;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.existsByEmail("admin@jobs.com")) return;
        createUser("Admin", "admin@jobs.com", "admin123", Role.ADMIN, null);
        User employer = createUser("TechNova HR", "employer@jobs.com", "employer123", Role.EMPLOYER, "TechNova");
        createUser("Demo Candidate", "candidate@jobs.com", "candidate123", Role.CANDIDATE, null);

        seed(employer, "Java Backend Developer", "Build REST APIs with Spring Boot and MySQL.", "Bhopal", "FULL_TIME", 600000);
        seed(employer, "Android Developer Intern", "Work on Java/Kotlin Android apps.", "Remote", "INTERNSHIP", 120000);
        seed(employer, "React Frontend Developer", "Build dashboards with React.", "Indore", "FULL_TIME", 700000);
    }

    private User createUser(String name, String email, String pw, Role role, String company) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(pw));
        u.setRole(role);
        u.setCompanyName(company);
        return users.save(u);
    }

    private void seed(User employer, String title, String desc, String location, String type, long salary) {
        Job j = new Job();
        j.setTitle(title);
        j.setDescription(desc);
        j.setCompany(employer.getCompanyName());
        j.setLocation(location);
        j.setJobType(type);
        j.setSalary(salary);
        j.setEmployer(employer);
        jobs.save(j);
    }
}
