package com.codealpha.urlshortener.service;

import com.codealpha.urlshortener.exception.ApiException;
import com.codealpha.urlshortener.model.UrlMapping;
import com.codealpha.urlshortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlMappingRepository repository;

    @Transactional
    public UrlMapping shorten(String url, String alias) {
        String normalized = validateUrl(url);
        String code;
        if (alias != null && !alias.isBlank()) {
            code = alias.trim();
            if (!code.matches("[A-Za-z0-9_-]{4,20}")) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Alias must be 4-20 characters: letters, digits, '-' or '_'");
            }
            if (repository.existsByShortCode(code)) {
                throw new ApiException(HttpStatus.CONFLICT, "Alias already in use");
            }
        } else {
            do {
                code = randomCode();
            } while (repository.existsByShortCode(code));
        }
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(code);
        mapping.setOriginalUrl(normalized);
        return repository.save(mapping);
    }

    @Transactional
    public String resolve(String code) {
        UrlMapping m = find(code);
        m.setClickCount(m.getClickCount() + 1);
        m.setLastAccessedAt(Instant.now());
        return m.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlMapping find(String code) {
        return repository.findByShortCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Short URL not found"));
    }

    @Transactional(readOnly = true)
    public List<UrlMapping> recent() {
        return repository.findTop20ByOrderByCreatedAtDesc();
    }

    @Transactional
    public void delete(String code) {
        repository.delete(find(code));
    }

    private String validateUrl(String raw) {
        String url = raw.trim();
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Invalid URL. It must start with http:// or https://");
        }
        if (url.length() > 2048) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "URL is too long");
        }
        return url;
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
