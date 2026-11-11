package com.codealpha.eventreg.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/", "/index.html", "/favicon.ico", "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}").permitAll()
                .anyRequest().authenticated())
            .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> {
                // JSON 401 without WWW-Authenticate so browsers do not show a native login popup
                res.setStatus(401);
                res.setContentType("application/json");
                res.getWriter().write("{\"status\":401,\"error\":\"Invalid or missing credentials\"}");
            }));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
