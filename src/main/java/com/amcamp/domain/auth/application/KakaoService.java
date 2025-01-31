package com.amcamp.domain.auth.application;

import com.amcamp.domain.auth.dto.response.IdTokenResponse;
import com.amcamp.infra.config.feign.KakaoOauthClient;
import com.amcamp.infra.config.oauth.KakaoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoProperties kakaoProperties;

    public String getIdToken(String code) {
        IdTokenResponse response = kakaoOauthClient.getIdToken(
                kakaoProperties.grantType(),
                kakaoProperties.clientId(),
                kakaoProperties.redirectUri(),
                code,
                kakaoProperties.clientSecret());

        return response.id_token();
    }
}
