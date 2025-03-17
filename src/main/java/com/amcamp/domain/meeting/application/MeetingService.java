package com.amcamp.domain.meeting.application;

import com.amcamp.domain.meeting.dao.MeetingRepository;
import com.amcamp.domain.meeting.domain.Meeting;
import com.amcamp.domain.meeting.dto.MeetingCreateRequest;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.MeetingErrorCode;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final TeamParticipantRepository teamParticipantRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final MeetingRepository meetingRepository;
    private final SprintRepository sprintRepository;
    private final MemberUtil memberUtil;

    // 미팅 생성
    public void createMeeting(Long sprintId, MeetingCreateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Sprint sprint = getValidateSprint(member, sprintId);
        validateMeetingDtInSprintPeriod(sprint, request.meetingDate());
        meetingRepository.save(
                Meeting.createMeeting(request.title(), request.meetingDate(), sprint));
    }

    // util
    private void validateMeetingDtInSprintPeriod(Sprint sprint, LocalDateTime meetingDt) {
        if (meetingDt.toLocalDate().isBefore(sprint.getToDoInfo().getStartDt())
                || meetingDt.toLocalDate().isAfter(sprint.getToDoInfo().getDueDt())) {
            throw new CommonException(MeetingErrorCode.INVALID_MEETING_DATE);
        }
    }

    private TeamParticipant getValidTeamParticipant(Member member, Team team) {
        return teamParticipantRepository
                .findByMemberAndTeam(member, team)
                .orElseThrow(() -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));
    }

    private ProjectParticipant validateProjectParticipant(Member member, Project project) {
        TeamParticipant teamParticipant = getValidTeamParticipant(member, project.getTeam());
        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .orElseThrow(
                        () -> new CommonException(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED));
    }

    private Sprint getValidateSprint(Member member, Long sprintId) {
        Sprint sprint =
                sprintRepository
                        .findById(sprintId)
                        .orElseThrow(() -> new CommonException(SprintErrorCode.SPRINT_NOT_FOUND));
        validateProjectParticipant(member, sprint.getProject());
        return sprint;
    }

    private Meeting getMeetingById(Long meetingId) {
        return meetingRepository
                .findMeetingById(meetingId)
                .orElseThrow(() -> new CommonException(MeetingErrorCode.MEETING_NOT_FOUND));
    }

    private boolean isMeetingExist(Long meetingId) {
        return meetingRepository.findMeetingById(meetingId).isPresent();
    }
}
