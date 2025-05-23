package com.amcamp.domain.auth.application;

import com.amcamp.domain.auth.domain.OauthProvider;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.AuthErrorCode;
import com.amcamp.infra.config.oauth.GoogleProperties;
import com.amcamp.infra.config.oauth.KakaoProperties;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdTokenVerifier {

    private final KakaoProperties kakaoProperties;
    private final GoogleProperties googleProperties;

    private final Map<OauthProvider, JwtDecoder> decoders =
            Map.of(
                    OauthProvider.GOOGLE, buildDecoder(OauthProvider.GOOGLE.getJwkSetUrl()),
                    OauthProvider.KAKAO, buildDecoder(OauthProvider.KAKAO.getJwkSetUrl()));

    private JwtDecoder buildDecoder(String jwkSetUrl) {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUrl).build();
    }

    public OidcUser getOidcUser(String idToken, OauthProvider provider) {
        Jwt jwt = getJwt(idToken, provider);
        OidcIdToken oidcIdToken = getOidcIdToken(jwt);

        validateAudience(oidcIdToken, provider.getClientId(googleProperties, kakaoProperties));
        validateIssuer(oidcIdToken, provider.getIssuer());
        validateExpiresAt(oidcIdToken);

        return new DefaultOidcUser(null, oidcIdToken);
    }

    private Jwt getJwt(String idToken, OauthProvider provider) {
        return decoders.get(provider).decode(idToken);
    }

    private OidcIdToken getOidcIdToken(Jwt jwt) {
        return new OidcIdToken(
                jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getClaims());
    }

    private void validateAudience(OidcIdToken oidcIdToken, String clientId) {
        String idTokenAudience = oidcIdToken.getAudience().get(0);

        if (idTokenAudience == null || !idTokenAudience.equals(clientId)) {
            throw new CommonException(AuthErrorCode.ID_TOKEN_VERIFICATION_FAILED);
        }
    }

    private void validateIssuer(OidcIdToken oidcIdToken, String issuer) {
        String idTokenIssuer = oidcIdToken.getIssuer().toString();

        if (idTokenIssuer == null || !idTokenIssuer.equals(issuer)) {
            throw new CommonException(AuthErrorCode.ID_TOKEN_VERIFICATION_FAILED);
        }
    }

    private void validateExpiresAt(OidcIdToken oidcIdToken) {
        Instant expiresAt = oidcIdToken.getExpiresAt();

        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            throw new CommonException(AuthErrorCode.ID_TOKEN_VERIFICATION_FAILED);
        }
    }
}
