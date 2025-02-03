package com.amcamp.domain.auth.application;

import com.amcamp.domain.auth.dto.response.IdTokenResponse;
import com.amcamp.infra.config.feign.GoogleOauthClient;
import com.amcamp.infra.config.oauth.GoogleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final GoogleOauthClient googleOauthClient;
    private final GoogleProperties googleProperties;

    public String getIdToken(String code) {
        IdTokenResponse response = googleOauthClient.getIdToken(
                googleProperties.grantType(),
                googleProperties.clientId(),
                googleProperties.redirectUri(),
                code,
                googleProperties.clientSecret());

        return response.id_token();
    }
}
