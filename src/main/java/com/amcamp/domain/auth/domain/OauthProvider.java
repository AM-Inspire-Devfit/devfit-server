package com.amcamp.domain.auth.domain;

import static com.amcamp.global.common.constants.SecurityConstants.*;

import com.amcamp.infra.config.oauth.GoogleProperties;
import com.amcamp.infra.config.oauth.KakaoProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OauthProvider {
    KAKAO(KAKAO_JWK_SET_URL, KAKAO_ISSUER),
    GOOGLE(GOOGLE_JWK_SET_URL, GOOGLE_ISSUER),
    ;

    private final String jwkSetUrl;
    private final String issuer;

    public String getClientId(GoogleProperties googleProperties, KakaoProperties kakaoProperties) {
        return switch (this) {
            case GOOGLE -> googleProperties.clientId();
            case KAKAO -> kakaoProperties.clientId();
        };
    }
}
