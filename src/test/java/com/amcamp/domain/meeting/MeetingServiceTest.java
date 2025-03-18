package com.amcamp.domain.meeting;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.meeting.application.MeetingService;
import com.amcamp.domain.meeting.dao.MeetingRepository;
import com.amcamp.domain.meeting.domain.Meeting;
import com.amcamp.domain.meeting.domain.MeetingStatus;
import com.amcamp.domain.meeting.dto.MeetingCreateRequest;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRegistrationRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.*;
import com.amcamp.domain.project.dto.request.ProjectCreateRequest;
import com.amcamp.domain.sprint.application.SprintService;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.dto.request.SprintCreateRequest;
import com.amcamp.domain.team.application.TeamService;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.dto.request.TeamCreateRequest;
import com.amcamp.domain.team.dto.request.TeamInviteCodeRequest;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.MeetingErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class MeetingServiceTest extends IntegrationTest {
    @Autowired private SprintService sprintService;
    @Autowired private ProjectService projectService;
    @Autowired private TeamService teamService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamParticipantRepository teamParticipantRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ProjectRegistrationRepository projectRegistrationRepository;
    @Autowired private ProjectParticipantRepository projectParticipantRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingService meetingService;

    private Member memberAdmin;
    private Member member1;
    private Member member2;
    private final String projectTitle = "projectTitle";
    private final String description = "projectDescription";
    private final String meetingTitle = "meetingTitle";
    private final LocalDate projectStartDt = LocalDate.of(2026, 1, 1);
    private final LocalDate projectDueDt = LocalDate.of(2026, 12, 1);
    private final LocalDate sprintStartDt = LocalDate.of(2026, 3, 1);
    private final LocalDate sprintDueDt = LocalDate.of(2026, 3, 31);
    private final LocalDateTime meetingDt = LocalDateTime.of(2026, 3, 15, 17, 0);

    private void loginAs(Member member) {
        UserDetails userDetails = new PrincipalDetails(member.getId(), member.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private void logout() {
        SecurityContextHolder.clearContext();
    }

    private TeamInviteCodeRequest teamInviteCodeRequest;

    // 팀 가입 및 아이디 생성
    private Long getTeamId() {
        TeamCreateRequest teamCreateRequest = new TeamCreateRequest("팀 이름", "팀 설명");
        String inviteCode = teamService.createTeam(teamCreateRequest).inviteCode();
        teamInviteCodeRequest = new TeamInviteCodeRequest(inviteCode);
        return teamService.getTeamByCode(teamInviteCodeRequest).teamId();
    }

    // 프로젝트 생성
    void createTestProject() {
        Long teamId = getTeamId();
        ProjectCreateRequest request =
                new ProjectCreateRequest(
                        teamId, projectTitle, projectStartDt, projectDueDt, description);

        projectService.createProject(request);
    }

    void createTestProject(Long teamId) {
        ProjectCreateRequest request =
                new ProjectCreateRequest(
                        teamId, projectTitle, projectStartDt, projectDueDt, description);

        projectService.createProject(request);
    }

    void createTestProject(
            Long teamId, String title, LocalDate startDt, LocalDate dueDt, String description) {
        ProjectCreateRequest request =
                new ProjectCreateRequest(teamId, title, startDt, dueDt, description);

        projectService.createProject(request);
    }

    // 스프린트 생성
    void createTestSprint(Long projectId) {
        SprintCreateRequest request =
                new SprintCreateRequest(projectId, "testSprintGoal", sprintStartDt, sprintDueDt);
        sprintService.createSprint(request);
    }

    // 미팅 생성
    void createTestMeeting(Long sprintId) {
        MeetingCreateRequest request = new MeetingCreateRequest(1L, meetingTitle, meetingDt);
        meetingService.createMeeting(request);
    }

    void createTestMeeting(Long sprintId, LocalDateTime meetingDt) {
        MeetingCreateRequest request = new MeetingCreateRequest(1L, meetingTitle, meetingDt);
        meetingService.createMeeting(request);
    }

    @BeforeEach
    public void setUp() {
        memberAdmin =
                Member.createMember(
                        "memberAdmin",
                        "testProfileImageUrl",
                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        memberRepository.save(memberAdmin);
        loginAs(memberAdmin);
        createTestProject();
        createTestSprint(1L);

        //        member1 =
        //                Member.createMember(
        //                        "member1",
        //                        "testProfileImageUrl",
        //                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        //        memberRepository.save(member1);
        //
        //        member2 =
        //                Member.createMember(
        //                        "member2",
        //                        "testProfileImageUrl",
        //                        OauthInfo.createOauthInfo("testOauthId", "testOauthProvider"));
        //        memberRepository.save(member2);

    }

    @AfterEach
    public void afterEach() {
        logout();
        projectRegistrationRepository.deleteAll();
        projectParticipantRepository.deleteAll();
        projectRepository.deleteAll();
        teamParticipantRepository.deleteAll();
        teamRepository.deleteAll();
        memberRepository.deleteAll();
        meetingRepository.deleteAll();
        sprintRepository.deleteAll();
    }

    @Test
    void 미팅을_생성하면_정상적으로_저장된다() {
        // given
        createTestMeeting(1L);
        // then
        Meeting meeting = meetingRepository.findById(1L).get();
        assertThat(meeting.getId()).isEqualTo(1L);
        assertThat(meeting)
                .extracting("id", "title", "meetingDt", "status")
                .containsExactlyInAnyOrder(1L, meetingTitle, meetingDt, MeetingStatus.OPEN);
    }

    @Test
    void 스프린트_시작일을_벗어나면_오류가_발생한다() {
        // given
        LocalDateTime wrongDtBeforeStartDt = LocalDateTime.of(2020, 3, 15, 17, 0);
        // then
        assertThatThrownBy(() -> createTestMeeting(1L, wrongDtBeforeStartDt))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.INVALID_MEETING_DATE.getMessage());
    }

    @Test
    void 스프린트_마감일을_벗어나면_오류가_발생한다() {
        // given
        LocalDateTime wrongDtAfterDueDt = LocalDateTime.of(2030, 3, 15, 17, 0);
        // then
        assertThatThrownBy(() -> createTestMeeting(1L, wrongDtAfterDueDt))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.INVALID_MEETING_DATE.getMessage());
    }
}
