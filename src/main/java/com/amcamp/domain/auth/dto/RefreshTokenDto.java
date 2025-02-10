package com.amcamp.domain.auth.dto;

public record RefreshTokenDto(Long memberId, String refreshTokenValue, Long ttl) {
    public static RefreshTokenDto of(Long memberId, String refreshTokenValue, Long ttl) {
        return new RefreshTokenDto(memberId, refreshTokenValue, ttl);
    }
}
