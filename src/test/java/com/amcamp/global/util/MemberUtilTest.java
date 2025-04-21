package com.amcamp.global.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.global.security.PrincipalDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class MemberUtilTest {

    @Autowired private MemberUtil memberUtil;
    @Autowired private MemberRepository memberRepository;

    private Member registerAuthenticatedMember() {
        Member member =
                Member.createMember(
                        "testNickName",
                        "testProfileImageUrl",
                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        memberRepository.save(member);

        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        return member;
    }

    @Test
    void 로그인한_멤버의_정보를_조회한다() {

        // given
        Member member = registerAuthenticatedMember();

        // when
        Member currentMember = memberUtil.getCurrentMember();

        // then
        assertThat(member.getId()).isEqualTo(currentMember.getId());
        assertThat(member.getRole()).isEqualTo(currentMember.getRole());
    }
}
