package com.amcamp.global.common.constants;

import java.security.SecureRandom;
import java.util.Base64;

public final class RedisConstants {
    public static final SecureRandom secureRandom = new SecureRandom();
    public static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static final String TEAM_ID_PREFIX = "teamId=%d";
    public static final String INVITE_CODE_PREFIX = "inviteCode=%s";
    public static final long expirationTime = 24;
}
