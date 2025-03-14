package com.amcamp.domain.contribution.application;

import com.amcamp.domain.contribution.dao.ContributionRepository;
import com.amcamp.domain.contribution.domain.Contribution;
import com.amcamp.domain.contribution.dto.response.BasicContributionInfoResponse;
import com.amcamp.domain.contribution.dto.response.ContributionInfoResponse;
import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.dao.SprintRepository;
import com.amcamp.domain.sprint.domain.Sprint;
import com.amcamp.domain.task.dao.TaskRepository;
import com.amcamp.domain.task.domain.TaskDifficulty;
import com.amcamp.domain.team.dao.TeamParticipantRepository;
import com.amcamp.domain.team.domain.Team;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.amcamp.global.exception.errorcode.TeamErrorCode;
import com.amcamp.global.util.MemberUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ContributionService {
    private final MemberUtil memberUtil;
    private final SprintRepository sprintRepository;
    private final ContributionRepository contributionRepository;
    private final TeamParticipantRepository teamParticipantRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final TaskRepository taskRepository;

    public BasicContributionInfoResponse getContributionByMember(Long sprintId) {
        Member member = memberUtil.getCurrentMember();
        Sprint sprint = findBySprintId(sprintId);
        Project project = sprint.getProject();
        ProjectParticipant participant =
                validateProjectParticipant(project, project.getTeam(), member);

        Contribution contribution = validateRank(sprint, participant);

        return BasicContributionInfoResponse.from(contribution);
    }

    public List<ContributionInfoResponse> getContributionBySprint(Long sprintId) {
        Member member = memberUtil.getCurrentMember();
        Sprint sprint = findBySprintId(sprintId);
        Project project = sprint.getProject();
        validateTeamParticipant(project.getTeam(), member);

        List<ProjectParticipant> participantList =
                projectParticipantRepository.findAllByProject(project);

        // 랭크 불러오기
        for (ProjectParticipant participant : participantList) {
            validateRank(sprint, participant);
        }
        List<Contribution> contributionList =
                contributionRepository.findBySprintOrderByScoreDesc(sprint);
        List<ContributionInfoResponse> result = new ArrayList<>();
        for (Contribution contribution : contributionList) {
            result.add(ContributionInfoResponse.from(contribution));
        }
        return result;
    }

    private void validateTeamParticipant(Team team, Member member) {
        TeamParticipant teamParticipant =
                teamParticipantRepository
                        .findByMemberAndTeam(member, team)
                        .orElseThrow(
                                () -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));
    }

    private ProjectParticipant validateProjectParticipant(
            Project project, Team team, Member currentMember) {
        TeamParticipant teamParticipant =
                teamParticipantRepository
                        .findByMemberAndTeam(currentMember, team)
                        .orElseThrow(
                                () -> new CommonException(TeamErrorCode.TEAM_PARTICIPANT_REQUIRED));

        return projectParticipantRepository
                .findByProjectAndTeamParticipant(project, teamParticipant)
                .orElseThrow(
                        () -> new CommonException(ProjectErrorCode.PROJECT_PARTICIPATION_REQUIRED));
    }

    private Contribution validateRank(Sprint sprint, ProjectParticipant participant) {
        Contribution rank = contributionRepository.findBySprintAndParticipant(sprint, participant);
        Double contribution = getContribution(sprint, participant);
        if (rank != null) {
            rank.updateContribution(contribution);
            return rank;
        } else {
            Contribution newContribution =
                    Contribution.createContribution(sprint, participant, contribution);
            contributionRepository.save(newContribution);
            return newContribution;
        }
    }

    private Double getContribution(Sprint sprint, ProjectParticipant participant) {
        int highTask = taskRepository.countBySprintAndTaskDifficulty(sprint, TaskDifficulty.HIGH);
        int midTask = taskRepository.countBySprintAndTaskDifficulty(sprint, TaskDifficulty.MID);
        int lowTask = taskRepository.countBySprintAndTaskDifficulty(sprint, TaskDifficulty.LOW);

        int highTaskCompleted =
                taskRepository.countBySprintAndAssigneeAndTaskDifficulty(
                        sprint, participant, TaskDifficulty.HIGH);
        int midTaskCompleted =
                taskRepository.countBySprintAndAssigneeAndTaskDifficulty(
                        sprint, participant, TaskDifficulty.MID);
        int lowTaskCompleted =
                taskRepository.countBySprintAndAssigneeAndTaskDifficulty(
                        sprint, participant, TaskDifficulty.LOW);

        int maxScore = 20 * highTask + 10 * midTask + lowTask * 5;
        if (maxScore == 0) {
            throw new CommonException(SprintErrorCode.TASK_NOT_CREATED_YET);
        }

        int total = (20 * highTaskCompleted + 10 * midTaskCompleted + 5 * lowTaskCompleted) * 100;
        return (double) (total / maxScore);
    }

    private Sprint findBySprintId(Long sprintId) {
        return sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new CommonException(SprintErrorCode.SPRINT_NOT_FOUND));
    }
}
