package com.clara.insurance_quote.auth.dto;

public record TokenResponseDto(
        String token,
        String tokenType,
        long expiresInSeconds
) {
}
