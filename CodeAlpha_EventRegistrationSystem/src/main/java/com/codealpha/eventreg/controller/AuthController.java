package com.codealpha.eventreg.controller;

import com.codealpha.eventreg.dto.Dtos.RegisterRequest;
import com.codealpha.eventreg.dto.Dtos.UserResponse;
import com.codealpha.eventreg.exception.ApiException;
import com.codealpha.eventreg.model.Role;
import com.codealpha.eventreg.model.User;
import com.codealpha.eventreg.repository.UserRepository;
import com.codealpha.eventreg.security.CurrentUser;
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

    /** Public sign-up. For this demo the caller may choose ORGANIZER or ATTENDEE. */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(req.password()));
        u.setRole(req.role() == null ? Role.ATTENDEE : req.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(users.save(u)));
    }

    /** Also used by the UI to verify credentials at login. */
    @GetMapping("/me")
    public UserResponse me(Authentication auth) {
        return toResponse(currentUser.from(auth));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
