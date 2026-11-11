package com.codealpha.jobboard.security;

import com.codealpha.jobboard.exception.ApiException;
import com.codealpha.jobboard.model.User;
import com.codealpha.jobboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository users;

    public User from(Authentication auth) {
        return users.findByEmail(auth.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
