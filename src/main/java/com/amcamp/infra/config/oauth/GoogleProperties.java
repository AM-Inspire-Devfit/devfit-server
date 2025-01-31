package com.amcamp.infra.config.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String grantType) {
}
