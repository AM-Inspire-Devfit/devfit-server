package com.amcamp.domain.feedback.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.amcamp.IntegrationTest;
import com.amcamp.domain.feedback.dao.FeedbackRepository;
import com.amcamp.domain.feedback.domain.Feedback;
import com.amcamp.domain.feedback.dto.request.FeedbackSendRequest;
import com.amcamp.domain.member.dao.MemberRepository;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.member.domain.OauthInfo;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.dao.ProjectRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.project.domain.ProjectParticipantRole;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.dao.TeamRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.domain.team.domain.TeamParticipantRole;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.FeedbackErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.security.PrincipalDetails;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public class FeedbackServiceTest extends IntegrationTest {

    private final String feedbackMessage = "이번 스프린트에서 아주 잘해주셨습니다.";

    private final LocalDate startDt = LocalDate.of(2026, 1, 2);
    private final LocalDate dueDt = LocalDate.of(2026, 3, 1);

    @Autowired private FeedbackService feedbackService;
    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamParticipantRepository teamParticipantRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectParticipantRepository projectParticipantRepository;

    private ProjectParticipant sender;
    private ProjectParticipant receiver;
    private ProjectParticipant anotherReceiver;
    private Sprint sprint;

    @BeforeEach
    void setUp() {
        Member senderMember =
                memberRepository.save(
                        Member.createMember(
                                "testSenderNickname",
                                "testSenderProfileImageUrl",
                                OauthInfo.createOauthInfo("testOauthId", "testOauthProvider")));
        Member receiverMember =
                memberRepository.save(
                        Member.createMember(
                                "testReceiverNickname",
                                "testReceiverProfileImageUrl",
                                OauthInfo.createOauthInfo("testOauthId", "testOauthProvider")));

        UserDetails userDetails =
                new PrincipalDetails(senderMember.getId(), senderMember.getRole());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        Team team = teamRepository.save(Team.createTeam("testName", "testDescription"));

        TeamParticipant teamParticipantAdmin =
                teamParticipantRepository.save(
                        TeamParticipant.createParticipant(
                                senderMember, team, TeamParticipantRole.ADMIN));
        TeamParticipant teamParticipantUser =
                teamParticipantRepository.save(
                        TeamParticipant.createParticipant(
                                receiverMember, team, TeamParticipantRole.USER));

        Project project =
                projectRepository.save(
                        Project.createProject(
                                team, "testTitle", "testDescription", startDt, dueDt));
        Project anotherProject =
                projectRepository.save(
                        Project.createProject(
                                team, "testTitle", "testDescription", startDt, dueDt));

        sender =
                projectParticipantRepository.save(
                        ProjectParticipant.createProjectParticipant(
                                teamParticipantAdmin,
                                project,
                                senderMember.getNickname(),
                                senderMember.getProfileImageUrl(),
                                ProjectParticipantRole.ADMIN));

        receiver =
                projectParticipantRepository.save(
                        ProjectParticipant.createProjectParticipant(
                                teamParticipantUser,
                                project,
                                senderMember.getNickname(),
                                senderMember.getProfileImageUrl(),
                                ProjectParticipantRole.MEMBER));

        anotherReceiver =
                projectParticipantRepository.save(
                        ProjectParticipant.createProjectParticipant(
                                teamParticipantUser,
                                anotherProject,
                                "test",
                                "test",
                                ProjectParticipantRole.ADMIN));

        sprint =
                sprintRepository.save(
                        Sprint.createSprint(project, "testSprint", "testGoal", startDt, dueDt));
    }

    @Nested
    class 피드백_메시지를_전송할_때 {

        @Test
        @Transactional
        void 입력_값이_정상이라면_피드백_메시지_전송에_성공한다() {
            // given
            FeedbackSendRequest request =
                    new FeedbackSendRequest(sprint.getId(), receiver.getId(), feedbackMessage);

            // when
            feedbackService.sendFeedback(request);

            // then
            Feedback feedback = feedbackRepository.findById(1L).get();
            assertThat(feedback.getSender()).isEqualTo(sender);
            assertThat(feedback.getReceiver()).isEqualTo(receiver);
            assertThat(feedback.getSprint()).isEqualTo(sprint);
            assertThat(feedback.getMessage()).isEqualTo(feedbackMessage);
        }

        @Test
        void 본인에게_피드백_메시지를_전송하면_예외가_발생한다() {
            // given
            FeedbackSendRequest request =
                    new FeedbackSendRequest(sprint.getId(), sender.getId(), feedbackMessage);

            // when & then
            assertThatThrownBy(() -> feedbackService.sendFeedback(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(FeedbackErrorCode.CANNOT_SEND_FEEDBACK_TO_SELF.getMessage());
        }

        @Test
        void 같은_스프린트에서_특정_대상에게_피드백_메시지를_두_번_전송하면_예외가_발생한다() {
            // given
            FeedbackSendRequest request =
                    new FeedbackSendRequest(sprint.getId(), receiver.getId(), feedbackMessage);
            feedbackService.sendFeedback(request);

            // when & then
            FeedbackSendRequest duplicateRequest =
                    new FeedbackSendRequest(sprint.getId(), receiver.getId(), feedbackMessage);
            assertThatThrownBy(() -> feedbackService.sendFeedback(duplicateRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(FeedbackErrorCode.FEEDBACK_ALREADY_SENT.getMessage());
        }

        @Test
        void 스프린트가_존재하지_않는다면_예외가_발생한다() {
            // given
            FeedbackSendRequest request =
                    new FeedbackSendRequest(999L, receiver.getId(), feedbackMessage);

            // when & then
            assertThatThrownBy(() -> feedbackService.sendFeedback(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(SprintErrorCode.SPRINT_NOT_FOUND.getMessage());
        }

        @Test
        void 피드백을_받을_대상이_존재하지_않는다면_예외가_발생한다() {
            // given
            FeedbackSendRequest request =
                    new FeedbackSendRequest(sprint.getId(), 999L, feedbackMessage);

            // when & then
            assertThatThrownBy(() -> feedbackService.sendFeedback(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(FeedbackErrorCode.RECEIVER_NOT_FOUND.getMessage());
        }

        @Test
        void 같은_프로젝트에_속하지_않은_대상에게_피드백_메시지를_전송하면_예외가_발생한다() {
            // given
            FeedbackSendRequest request =
                    new FeedbackSendRequest(
                            sprint.getId(), anotherReceiver.getId(), feedbackMessage);

            // when & then
            assertThatThrownBy(() -> feedbackService.sendFeedback(request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(FeedbackErrorCode.INVALID_PROJECT_PARTICIPANT.getMessage());
        }
    }
}
