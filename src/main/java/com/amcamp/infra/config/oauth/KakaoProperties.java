package com.amcamp.infra.config.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String grantType) {
}
