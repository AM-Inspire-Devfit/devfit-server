package com.amcamp.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.amcamp.global.util.RedisUtil;
import java.util.Optional;

import static com.amcamp.global.common.constants.RedisConstants.*;

@Component
@RequiredArgsConstructor
public class RandomUtil {
	private final RedisUtil redisUtil;
	private static String codeGenerator(int length){
		byte[] randomBytes = new byte[length];
		secureRandom.nextBytes(randomBytes);
		return base64Encoder.encodeToString(randomBytes);
	}
	public String generateCode(Long teamId) {
		final Optional<String> existingCode = redisUtil.getData(TEAM_ID_PREFIX.formatted(teamId));

		if (existingCode.isEmpty()) {
			String inviteCode = codeGenerator(6);
			redisUtil.setDataExpire(TEAM_ID_PREFIX.formatted(teamId), inviteCode, expirationTime);
			redisUtil.setDataExpire(INVITE_CODE_PREFIX.formatted(inviteCode), teamId.toString(), expirationTime);
			return inviteCode;
		}
		return existingCode.get();
	}
}
