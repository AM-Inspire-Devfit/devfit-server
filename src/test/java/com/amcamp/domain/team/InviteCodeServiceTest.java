//package com.amcamp.domain.team;
//
//import com.amcamp.domain.team.application.InviteCodeServiceImpl;
//import com.amcamp.domain.team.dao.TeamRepository;
//import com.amcamp.domain.team.domain.Team;
//import com.amcamp.global.exception.CommonException;
//import com.amcamp.global.exception.errorcode.TeamErrorCode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import static org.mockito.Mockito.*;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class InviteCodeServiceImplTest {
//	@Autowired
//	private StringRedisTemplate redisTemplate;
//	@Autowired
//	private TeamRepository teamRepository;
//	@Autowired
//	private InviteCodeServiceImpl inviteCodeService;
//
//	private Team team;
//
//	@BeforeEach
//	void setUp() {
//		team = Team.createTeam("name", "descr");
//		teamRepository.save(team);
//	}
////	@Nested
////	class 초대코드_생성_시 {
//		@Test
//		void 초대코드가_없을_경우_새로운_초대코드_생성() {
//			// Given: 초대 코드 유무 확인
//			assertNull(redisTemplate.opsForValue().get("inviteCode:" + team.getId()));
//
//			// When
//			String result = inviteCodeService.generateCode(team.getId());
//
//			// Then
//			assertNotNull(result);
//			assertTrue(result.length() > 0);
//			assertEquals(result, redisTemplate.opsForValue().get("inviteCode:" + team.getId()));
//		}
//		@Test
//		void generateCode_팀이_이미_초대코드를_가지고_있을_경우_초대코드_반환() {
//			// Given: 초대 코드 유무 확인
//			assertNotNull(redisTemplate.opsForValue().get("inviteCode:" + team.getId()));
//			String existingCode = redisTemplate.opsForValue().get("inviteCode:" + team.getId());
//			// When
//			String result = inviteCodeService.generateCode(team.getId());
//
//			// Then
//			assertEquals(existingCode, result);
//		}
////	}
//
////	@Nested
////	class 초대코드로_팀_검색_시{
//		@Test
//		void searchTeamByCode_초대코드가_유효할_경우_팀_반환() {
//			// Given
//			Team savedTeam = teamRepository.findById(team.getId())
//				.orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_NOT_FOUND));
//			String inviteCode = redisTemplate.opsForValue().get("inviteCode:"+ savedTeam.getId());
//
//			// When
//			Team result = inviteCodeService.searchTeamByCode(inviteCode);
//
//			// Then
//			assertNotNull(result);
//			assertEquals(savedTeam.getId(), result.getId());
//			assertEquals(savedTeam.getTeam_name(), result.getTeam_name());
//			assertEquals(savedTeam.getTeam_description(), result.getTeam_description());
//		}
//
//		@Test
//		void searchTeamByCode_유효하지_않은_초대코드의_경우_예외_발생() {
//			// Given
//			String inviteCode = "INVALID_CODE";
//
//			// When & Then
//			CommonException exception = assertThrows(CommonException.class, () -> inviteCodeService.searchTeamByCode(inviteCode));
//			assertEquals(TeamErrorCode.INVALID_INVITE_CODE, exception.getErrorCode());
//		}
//	}
//
//
