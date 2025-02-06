package com.amcamp.domain.member.application;

import com.amcamp.domain.auth.dao.RefreshTokenRepository;
import com.amcamp.domain.auth.domain.RefreshToken;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberStatus;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import com.amcamp.global.util.MemberUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

	@Autowired
	private MemberUtil memberUtil;
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	private Member registerAuthenticatedMember() {
		Member member = Member.createMember("testNickName", "testProfileImageUrl",
			OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
		memberRepository.save(member);

		UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		return member;
	}

	@Nested
	class 회원_탈퇴_시 {
		@Test
		void 탈퇴하지_않은_유저면_성공한다() {
			// given
			Member member = registerAuthenticatedMember();

			RefreshToken refreshToken =
				RefreshToken.builder().memberId(member.getId()).token("testRefreshToken").build();
			refreshTokenRepository.save(refreshToken);

			// when
			memberService.withdrawalMember();
			Member currentMember = memberRepository.findById(member.getId()).get();

			// then
			assertThat(refreshTokenRepository.findById(member.getId())).isEmpty();
			assertThat(currentMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		}

		@Test
		void 탈퇴한_유저면_예외가_발생한다() {
			// given
			registerAuthenticatedMember();
			memberService.withdrawalMember();

			// when & then
			assertThatThrownBy(() -> memberService.withdrawalMember())
				.isInstanceOf(CommonException.class)
				.hasMessage(MemberErrorCode.MEMBER_ALREADY_DELETED.getMessage());
		}
	}
}
