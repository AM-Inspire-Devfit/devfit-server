package com.amcamp.domain.auth.application;

import com.amcamp.domain.auth.dao.RefreshTokenRepository;
import com.amcamp.domain.auth.domain.RefreshToken;
import com.amcamp.domain.auth.dto.AccessTokenDto;
import com.amcamp.domain.auth.dto.RefreshTokenDto;
import com.amcamp.domain.member.domain.MemberRole;
import com.amcamp.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.amcamp.global.common.constants.SecurityConstants.TOKEN_ROLE_NAME;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public AccessTokenDto createAccessTokenDto(Long memberId, MemberRole memberRole) {
        return jwtUtil.generateAccessTokenDto(memberId, memberRole);
    }

    public String createAccessToken(Long memberId, MemberRole memberRole) {
        return jwtUtil.generateAccessToken(memberId, memberRole);
    }

    public RefreshTokenDto createRefreshTokenDto(Long memberId) {
        RefreshTokenDto refreshTokenDto = jwtUtil.generateRefreshTokenDto(memberId);
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .memberId(memberId)
                        .token(refreshTokenDto.refreshTokenValue())
                        .ttl(refreshTokenDto.ttl())
                        .build();
        refreshTokenRepository.save(refreshToken);

        return refreshTokenDto;
    }

    public String createRefreshToken(Long memberId) {
        String token = jwtUtil.generateRefreshToken(memberId);
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .memberId(memberId)
                        .token(token)
                        .ttl(jwtUtil.getRefreshTokenExpirationTime())
                        .build();
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public AccessTokenDto retrieveAccessToken(String accessTokenValue) {
        try {
            return jwtUtil.parseAccessToken(accessTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    public RefreshTokenDto retrieveRefreshToken(String refreshTokenValue) {
        RefreshTokenDto refreshTokenDto = parseRefreshToken(refreshTokenValue);

        if (refreshTokenDto == null) {
            return null;
        }

        Optional<RefreshToken> refreshToken = getRefreshToken(refreshTokenDto.memberId());

        if (refreshToken.isPresent() &&
                refreshTokenDto.refreshTokenValue().equals(refreshToken.get().getToken())) {
            return refreshTokenDto;
        }

        return null;
    }

    public AccessTokenDto reissueAccessTokenIfExpired(String accessTokenValue) {
        try {
            jwtUtil.parseAccessToken(accessTokenValue);
            return null;
        } catch (ExpiredJwtException e) {
            Long memberId = Long.parseLong(e.getClaims().getSubject());
            MemberRole memberRole = MemberRole.valueOf(e.getClaims().get(TOKEN_ROLE_NAME, String.class));

            return createAccessTokenDto(memberId, memberRole);
        }
    }

    private RefreshTokenDto parseRefreshToken(String refreshTokenValue) {
        try {
            return jwtUtil.parseRefreshToken(refreshTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    private Optional<RefreshToken> getRefreshToken(Long memberId) {
        return refreshTokenRepository.findById(memberId);
    }
}
