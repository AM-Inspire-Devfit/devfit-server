package com.amcamp.domain.meeting;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.meeting.application.MeetingService;
import com.amcamp.domain.meeting.dao.MeetingRepository;
import com.amcamp.domain.meeting.domain.Meeting;
import com.amcamp.domain.meeting.domain.MeetingStatus;
import com.amcamp.domain.meeting.dto.request.MeetingCreateRequest;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.application.ProjectService;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRegistrationRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
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
    private final String projectTitle = "projectTitle";
    private final String description = "projectDescription";
    private final String meetingTitle = "meetingTitle";
    private final LocalDate projectDueDt = LocalDate.of(2026, 12, 1);
    private final LocalDate sprintDueDt = LocalDate.of(2026, 3, 31);
    private final LocalDateTime meetingStart = LocalDateTime.of(2026, 3, 15, 17, 0);
    private final LocalDateTime meetingEnd = LocalDateTime.of(2026, 3, 15, 18, 0);

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
                new ProjectCreateRequest(teamId, projectTitle, projectDueDt, description);

        projectService.createProject(request);
    }

    void createTestProject(Long teamId) {
        ProjectCreateRequest request =
                new ProjectCreateRequest(teamId, projectTitle, projectDueDt, description);

        projectService.createProject(request);
    }

    void createTestProject(Long teamId, String title, LocalDate dueDt, String description) {
        ProjectCreateRequest request = new ProjectCreateRequest(teamId, title, dueDt, description);

        projectService.createProject(request);
    }

    // 스프린트 생성
    void createTestSprint(Long projectId) {
        SprintCreateRequest request =
                new SprintCreateRequest(projectId, "testSprintGoal", sprintDueDt);
        sprintService.createSprint(request);
    }

    // 미팅 생성
    void createTestMeeting(Long sprintId) {
        MeetingCreateRequest request =
                new MeetingCreateRequest(sprintId, meetingTitle, meetingStart, meetingEnd);
        meetingService.createMeeting(request);
    }

    void createTestMeeting(Long sprintId, LocalDateTime meetingStart, LocalDateTime meetingEnd) {
        MeetingCreateRequest request =
                new MeetingCreateRequest(sprintId, meetingTitle, meetingStart, meetingEnd);
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
                .extracting("id", "title", "meetingStart", "meetingEnd", "status")
                .containsExactlyInAnyOrder(
                        1L, meetingTitle, meetingStart, meetingEnd, MeetingStatus.OPEN);
    }

    @Test
    void 스프린트_시작일을_벗어나면_오류가_발생한다() {
        // given
        LocalDateTime wrongDtBeforeStartDt = LocalDateTime.of(2020, 3, 15, 17, 0);

        // then
        assertThatThrownBy(() -> createTestMeeting(1L, wrongDtBeforeStartDt, meetingEnd))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_DATE_OUT_OF_SPRINT.getMessage());
    }

    @Test
    void 스프린트_마감일을_벗어나면_오류가_발생한다() {
        // given
        LocalDateTime wrongDtAfterDueDt = LocalDateTime.of(2030, 3, 15, 17, 0);
        // then
        assertThatThrownBy(() -> createTestMeeting(1L, meetingStart, wrongDtAfterDueDt))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_DATE_OUT_OF_SPRINT.getMessage());
    }

    @Test
    void 시작시간이_종료시간보다_느리거나_같으면_오류가_발생한다() {

        // then
        assertThatThrownBy(() -> createTestMeeting(1L, meetingEnd, meetingStart))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.INVALID_MEETING_TIME_RANGE.getMessage());
        assertThatThrownBy(() -> createTestMeeting(1L, meetingStart, meetingStart))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.INVALID_MEETING_TIME_RANGE.getMessage());
    }

    @Test
    void 시간이_8시부터_00시_범위를_벗어나면_오류가_발생한다() {
        // given
        createTestMeeting(1L); // 기존 meetingStart, meetingEnd로 생성

        // when
        LocalDateTime before8 = LocalDateTime.of(2026, 3, 15, 1, 0);
        LocalDateTime after0 = LocalDateTime.of(2026, 3, 16, 0, 1);
        // then
        assertThatThrownBy(() -> createTestMeeting(1L, before8, meetingEnd))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.INVALID_MEETING_TIME_RANGE.getMessage());
        assertThatThrownBy(() -> createTestMeeting(1L, meetingStart, after0))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.INVALID_MEETING_TIME_RANGE.getMessage());
    }

    @Test
    void 기존_미팅과_일시가_겹치면_오류가_발생한다() {
        // given
        createTestMeeting(1L); // 기존 meetingStart, meetingEnd로 생성

        // when
        LocalDateTime beforeStart = LocalDateTime.of(2026, 3, 15, 16, 0);
        LocalDateTime afterEnd = LocalDateTime.of(2026, 3, 15, 19, 0);
        LocalDateTime afterStart = LocalDateTime.of(2026, 3, 15, 17, 20);
        LocalDateTime beforeEnd = LocalDateTime.of(2026, 3, 15, 17, 40);
        // then
        assertThatThrownBy(() -> createTestMeeting(1L, meetingStart, meetingEnd))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_ALREADY_EXISTS.getMessage());
        assertThatThrownBy(() -> createTestMeeting(1L, beforeStart, meetingEnd))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_ALREADY_EXISTS.getMessage());
        assertThatThrownBy(() -> createTestMeeting(1L, meetingStart, afterEnd))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_ALREADY_EXISTS.getMessage());
        assertThatThrownBy(() -> createTestMeeting(1L, afterStart, beforeEnd))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_ALREADY_EXISTS.getMessage());
    }
}
