package com.amcamp.global.util;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberRole;
import com.amcamp.global.security.PrincipalDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class MemberUtilTest {

	@Autowired
	private MemberUtil memberUtil;
	@Autowired
	private MemberRepository memberRepository;

	@Test
	void 로그인한_멤버의_정보를_조회한다() {

		// given
		UserDetails userDetails = new PrincipalDetails(1L, MemberRole.USER);
		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		Member savedMember =
			memberRepository.save(Member.createMember("", null, null));

		// when
		Member currentMember = memberUtil.getCurrentMember();

		// then
		Assertions.assertEquals(savedMember.getId(), currentMember.getId());
		Assertions.assertEquals(savedMember.getRole(), currentMember.getRole());
	}
}
