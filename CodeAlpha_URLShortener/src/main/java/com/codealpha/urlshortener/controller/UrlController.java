package com.codealpha.urlshortener.controller;

import com.codealpha.urlshortener.dto.Dtos.ShortenRequest;
import com.codealpha.urlshortener.dto.Dtos.ShortenResponse;
import com.codealpha.urlshortener.model.UrlMapping;
import com.codealpha.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService service;

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest req,
                                                   HttpServletRequest http) {
        UrlMapping m = service.shorten(req.url(), req.alias());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(m, http));
    }

    @GetMapping("/api/urls")
    public List<ShortenResponse> recent(HttpServletRequest http) {
        return service.recent().stream().map(m -> toResponse(m, http)).toList();
    }

    @GetMapping("/api/stats/{code}")
    public ShortenResponse stats(@PathVariable String code, HttpServletRequest http) {
        return toResponse(service.find(code), http);
    }

    @DeleteMapping("/api/urls/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }

    /** Redirect route: http://host/{code} -> original URL. The regex keeps it from clashing with static files. */
    @GetMapping("/{code:[A-Za-z0-9_-]{4,20}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String target = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(target).toString())
                .build();
    }

    private ShortenResponse toResponse(UrlMapping m, HttpServletRequest http) {
        String base = ServletUriComponentsBuilder.fromContextPath(http).build().toUriString();
        return new ShortenResponse(m.getShortCode(), base + "/" + m.getShortCode(),
                m.getOriginalUrl(), m.getClickCount(), m.getCreatedAt());
    }
}
