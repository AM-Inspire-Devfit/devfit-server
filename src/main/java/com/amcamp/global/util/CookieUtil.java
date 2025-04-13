package com.amcamp.global.util;

import static com.amcamp.global.common.constants.SecurityConstants.REFRESH_TOKEN_COOKIE_NAME;

import com.amcamp.global.helper.SpringEnvironmentHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final SpringEnvironmentHelper springEnvironmentHelper;

    public HttpHeaders generateRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                        .path("/")
                        //					                        .secure(true)
                        //					                        .sameSite(determineSameSitePolicy())
                        .secure(false)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders deleteRefreshTokenCookie() {
        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                        .path("/")
                        .maxAge(0)
                        //                        .secure(true)
                        //                        .sameSite(determineSameSitePolicy())
                        .secure(false)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .httpOnly(true)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private String determineSameSitePolicy() {
        if (springEnvironmentHelper.isProdProfile()) {
            //            return Cookie.SameSite.STRICT.attributeValue();
            return Cookie.SameSite.NONE.attributeValue();
        }
        return Cookie.SameSite.NONE.attributeValue();
    }
}
