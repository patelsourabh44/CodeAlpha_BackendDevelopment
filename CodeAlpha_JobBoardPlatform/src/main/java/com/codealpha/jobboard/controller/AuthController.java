package com.codealpha.jobboard.controller;

import com.codealpha.jobboard.dto.Dtos.RegisterRequest;
import com.codealpha.jobboard.dto.Dtos.UserResponse;
import com.codealpha.jobboard.exception.ApiException;
import com.codealpha.jobboard.model.Role;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.repository.UserRepository;
import com.codealpha.jobboard.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final CurrentUser currentUser;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (req.role() == Role.ADMIN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Role must be EMPLOYER or CANDIDATE");
        }
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(req.password()));
        u.setRole(req.role());
        if (req.role() == Role.EMPLOYER) {
            u.setCompanyName(req.companyName() == null || req.companyName().isBlank()
                    ? req.name().trim() : req.companyName().trim());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(users.save(u)));
    }

    @GetMapping("/me")
    public UserResponse me(Authentication auth) {
        return toResponse(currentUser.from(auth));
    }

    static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(),
                u.getCompanyName(), u.getResumeOriginalName());
    }
}
