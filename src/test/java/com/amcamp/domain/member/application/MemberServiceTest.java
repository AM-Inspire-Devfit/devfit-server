package com.amcamp.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.auth.dao.RefreshTokenRepository;
import com.amcamp.domain.auth.domain.RefreshToken;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.MemberRole;
import com.amcamp.domain.member.domain.MemberStatus;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.member.dto.request.NicknameUpdateRequest;
import com.amcamp.domain.member.dto.response.BasicMemberResponse;
import com.amcamp.domain.member.dto.response.MemberInfoResponse;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.domain.team.dto.response.TeamCheckResponse;
import com.amcamp.domain.team.dto.response.TeamInviteCodeResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.MemberErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class MemberServiceTest extends IntegrationTest {

    @Autowired private MemberService memberService;
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    private Member registerAuthenticatedMember() {
        Member member =
                Member.createMember(
                        "testNickname",
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

    private void loginAs(Member member) {
        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Nested
    class 로그아웃_시 {
        @Test
        void 로그아웃하면_리프레시_토큰이_삭제된다() {
            // given
            Member member = registerAuthenticatedMember();

            RefreshToken refreshToken =
                    RefreshToken.builder()
                            .memberId(member.getId())
                            .token("testRefreshToken")
                            .build();
            refreshTokenRepository.save(refreshToken);

            // when
            memberService.logoutMember();

            // then
            assertThat(refreshTokenRepository.findById(member.getId())).isEmpty();
        }
    }

    @Nested
    class 회원_탈퇴_시 {
        @Test
        void 탈퇴하지_않은_유저면_성공한다() {
            // given
            Member member = registerAuthenticatedMember();

            RefreshToken refreshToken =
                    RefreshToken.builder()
                            .memberId(member.getId())
                            .token("testRefreshToken")
                            .build();
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

    @Nested
    class 회원_닉네임_변경_시 {
        @Test
        void 닉네임이_NULL_이면_예외가_발생한다() {
            // given
            registerAuthenticatedMember();
            NicknameUpdateRequest request = new NicknameUpdateRequest(null);

            // when
            memberService.updateMemberNickname(request);
            Set<ConstraintViolation<NicknameUpdateRequest>> violations =
                    Validation.buildDefaultValidatorFactory().getValidator().validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getMessage()).isEqualTo("닉네임은 비워둘 수 없습니다.");
        }

        @Test
        void 유효한_입력값이면_닉네임이_변경된다() {
            // given
            Member member = registerAuthenticatedMember();
            NicknameUpdateRequest request = new NicknameUpdateRequest("현태 최");

            // when
            memberService.updateMemberNickname(request);
            Member currentMember = memberRepository.findById(member.getId()).get();

            // then
            assertThat(currentMember.getNickname()).isEqualTo("현태 최");
        }
    }

    @Test
    void 회원_정보를_조회한다() {
        // given
        registerAuthenticatedMember();

        // when
        MemberInfoResponse response = memberService.getMemberInfo();

        // then
        assertThat(response.nickname()).isEqualTo("testNickname");
        assertThat(response.profileImageUrl()).isEqualTo("testProfileImageUrl");
        assertThat(response.role()).isEqualTo(MemberRole.USER);
        assertThat(response.status()).isEqualTo(MemberStatus.NORMAL);
    }

    @Nested
    class 내가_속한_팀의_멤버를_조회_시 {
        @Test
        void 팀장을_포함한_멤버가_1명() {
            // given
            registerAuthenticatedMember();
            TeamInviteCodeResponse teamInviteCodeResponse =
                    teamService.createTeam(TeamCreateRequest.of("MyTeam", "This is my team"));
            String inviteCode = teamInviteCodeResponse.inviteCode();
            TeamCheckResponse teamCheckResponse =
                    teamService.getTeamByCode(new TeamInviteCodeRequest(inviteCode));

            // when
            Slice<BasicMemberResponse> results =
                    memberService.findAllMembers(teamCheckResponse.teamId(), null, 3);

            // then
            assertThat(results.getContent()).hasSize(0);
        }

        @Test
        void 팀장을_제외한_멤버가_3명() {
            // given
            registerAuthenticatedMember();
            TeamInviteCodeResponse teamInviteCodeResponse =
                    teamService.createTeam(TeamCreateRequest.of("MyTeam", "This is my team"));
            String inviteCode = teamInviteCodeResponse.inviteCode();
            TeamCheckResponse teamCheckResponse =
                    teamService.getTeamByCode(new TeamInviteCodeRequest(inviteCode));

            List<Member> requests =
                    List.of(
                            Member.createMember("member1", "url1", null), // 2L
                            Member.createMember("member2", "url2", null), // 3L
                            Member.createMember("member3", "url3", null), // 4L
                            Member.createMember("member4", "url4", null), // 5L
                            Member.createMember("member5", "url5", null)); // 6L

            for (Member member : requests) {
                memberRepository.save(member);
                loginAs(member);
                teamService.joinTeam(new TeamInviteCodeRequest(inviteCode));
            }

            // when
            Slice<BasicMemberResponse> results =
                    memberService.findAllMembers(teamCheckResponse.teamId(), null, 2);

            // then
            assertThat(results.getContent()).hasSize(2);
            assertThat(results)
                    .extracting("memberId", "nickname", "profileImageUrl")
                    .containsExactlyInAnyOrder(
                            tuple(6L, "member5", "url5"), tuple(5L, "member4", "url4"));
        }
    }
}
