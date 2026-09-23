package com.clara.insurance_quote.auth.controller;


import com.clara.insurance_quote.auth.dto.TokenResponseDto;
import com.clara.insurance_quote.auth.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> getGuestToken() {
        String token = jwtTokenProvider.generateGuestToken();
        long expiresInSeconds = jwtTokenProvider.getExpirationInMs() / 1000;

        return ResponseEntity.ok(new TokenResponseDto(token, "Bearer", expiresInSeconds));
    }
}
