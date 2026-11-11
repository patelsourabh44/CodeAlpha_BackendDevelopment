package com.codealpha.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public final class Dtos {
    private Dtos() {}

    public record ShortenRequest(@NotBlank String url, String alias) {}

    public record ShortenResponse(String shortCode, String shortUrl, String originalUrl,
                                  long clicks, Instant createdAt) {}
}
