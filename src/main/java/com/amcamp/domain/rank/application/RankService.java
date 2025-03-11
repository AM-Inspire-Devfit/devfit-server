package com.amcamp.domain.rank.application;

import com.amcamp.domain.member.domain.Member;
import com.amcamp.domain.project.dao.ProjectParticipantRepository;
import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.rank.dao.RankRepository;
import com.amcamp.domain.rank.domain.Rank;
import com.amcamp.domain.rank.dto.response.BasicRankInfoResponse;
import com.amcamp.domain.rank.dto.response.RankInfoResponse;
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
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class RankService {
    private final MemberUtil memberUtil;
    private final SprintRepository sprintRepository;
    private final RankRepository rankRepository;
    private final TeamParticipantRepository teamParticipantRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final TaskRepository taskRepository;

    public BasicRankInfoResponse getRankByMember(Long sprintId) {
        Member member = memberUtil.getCurrentMember();
        Sprint sprint = findBySprintId(sprintId);
        Project project = sprint.getProject();
        ProjectParticipant participant =
                validateProjectParticipant(project, project.getTeam(), member);

        Rank rank = validateRank(sprint, participant);

        return BasicRankInfoResponse.from(rank);
    }

    public List<RankInfoResponse> getRankBySprint(Long sprintId) {
        Member member = memberUtil.getCurrentMember();
        Sprint sprint = findBySprintId(sprintId);
        Project project = sprint.getProject();
        validateProjectParticipant(project, project.getTeam(), member);

        List<ProjectParticipant> participantList =
                projectParticipantRepository.findAllByProject(project);

        // 랭크 불러오기
        List<Rank> rankList = new ArrayList<>();
        for (ProjectParticipant participant : participantList) {
            Rank rank = validateRank(sprint, participant);
            rankList.add(rank);
        }

        // 등위 처리
        rankList.sort(Comparator.comparingDouble(Rank::getContribution).reversed());

        int placement = 1;
        for (Rank rank : rankList) {
            rank.updatePlacement(placement++);
        }

        List<RankInfoResponse> rankInfoResponseList = new ArrayList<>();
        for (Rank rank : rankList) {
            rankInfoResponseList.add(RankInfoResponse.from(rank));
        }
        return rankInfoResponseList;
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

    private Rank validateRank(Sprint sprint, ProjectParticipant participant) {
        Rank rank = rankRepository.findBySprintAndParticipant(sprint, participant);
        Double contribution = getContribution(sprint, participant);
        if (rank != null) {
            rank.updateContribution(contribution);
            return rank;
        } else {
            Rank newRank = Rank.createRank(sprint, participant, contribution);
            rankRepository.save(newRank);
            return newRank;
        }
    }

    private Double getContribution(Sprint sprint, ProjectParticipant participant) {
        int highTask = taskRepository.countBySprintAndTaskDifficulty(sprint, TaskDifficulty.HIGH);
        int midTask = taskRepository.countBySprintAndTaskDifficulty(sprint, TaskDifficulty.MID);
        int lowTask = taskRepository.countBySprintAndTaskDifficulty(sprint, TaskDifficulty.LOW);

        int highTaskCompleted =
                taskRepository.countBySprintAndTaskDifficultyAndAssignee(
                        sprint, participant, TaskDifficulty.HIGH);
        int midTaskCompleted =
                taskRepository.countBySprintAndTaskDifficultyAndAssignee(
                        sprint, participant, TaskDifficulty.MID);
        int lowTaskCompleted =
                taskRepository.countBySprintAndTaskDifficultyAndAssignee(
                        sprint, participant, TaskDifficulty.LOW);

        int maxScore = 20 * highTask + 10 * midTask + lowTask * 5;
        int total = (20 * highTaskCompleted + 10 * midTaskCompleted + 5 * lowTaskCompleted) * 100;
        return (double) (total / maxScore);
    }

    private Sprint findBySprintId(Long sprintId) {
        return sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new CommonException(SprintErrorCode.SPRINT_NOT_FOUND));
    }
}
