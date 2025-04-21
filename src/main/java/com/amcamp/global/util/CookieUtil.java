package com.amcamp.global.util;

import static com.amcamp.global.common.constants.SecurityConstants.REFRESH_TOKEN_COOKIE_NAME;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public HttpHeaders generateRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                        .domain(".devfit.site")
                        .path("/")
                        .secure(true)
                        .sameSite(determineSameSitePolicy())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders deleteRefreshTokenCookie() {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                        .domain(".devfit.site")
                        .path("/")
                        .maxAge(0)
                        .secure(true)
                        .sameSite(determineSameSitePolicy())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private String determineSameSitePolicy() {
        return Cookie.SameSite.NONE.attributeValue();
    }
}
