package com.amcamp.domain.team.application;

import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class InviteCodeServiceImpl implements InviteCodeService{

	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final SecureRandom random = new SecureRandom();
	@Value("${invite.code.expiration:86400}")
	private long expirationTime;
	private final StringRedisTemplate redisTemplate;
	private final TeamRepository teamRepository;

	@Override
	public String generateCode(Long teamId) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));

		String existingCode = redisTemplate.opsForValue().get("inviteCode:" + team.getId());
		if (existingCode != null) {
			return existingCode;
		}
		else {
			String inviteCode = codeGenerator(6);

			redisTemplate.opsForValue().set("teamId:" + inviteCode, team.getId().toString(), expirationTime, TimeUnit.SECONDS);
			redisTemplate.opsForValue().set("inviteCode:" + team.getId(), inviteCode, expirationTime, TimeUnit.SECONDS);
			return inviteCode;
		}
	}
	@Override
	public Team searchTeamByCode(String inviteCode){
//		Long teamId = Long.valueOf(redisTemplate.opsForValue().get("teamId:" + inviteCode));
		Long teamId = Optional.ofNullable(redisTemplate.opsForValue().get("teamId:" + inviteCode))
			.map(Long::valueOf)
			.orElseThrow(() -> new CommonException(TeamErrorCode.INVALID_INVITE_CODE));

		if (teamId == null) { throw new CommonException(TeamErrorCode.INVALID_INVITE_CODE); }
		Team team = teamRepository.findById(teamId).orElseThrow(() ->
			new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
		return team;
	}

	private String codeGenerator (int length){
		StringBuilder code = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(ALPHABET.length());
			code.append(ALPHABET.charAt(index));
		}
		return code.toString();
	}
}
