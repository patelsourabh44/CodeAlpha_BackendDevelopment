package com.codealpha.eventreg.security;

import com.codealpha.eventreg.exception.ApiException;
import com.codealpha.eventreg.model.User;
import com.codealpha.eventreg.repository.UserRepository;
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
