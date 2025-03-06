package com.amcamp.domain.project.dao;

import static com.amcamp.domain.project.domain.QProject.project;

import com.amcamp.domain.project.domain.Project;
import com.amcamp.domain.project.domain.QProject;
import com.amcamp.domain.project.domain.QProjectParticipant;
import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final QProject qProject = QProject.project;
    private final QProjectParticipant qProjectParticipant = QProjectParticipant.projectParticipant;

    @Override
    public List<Project> findAllByTeamId(Long teamId) {
        return queryFactory.selectFrom(project).where(project.team.id.eq(teamId)).fetch();
    }

    @Override
    public Slice<ProjectInfoResponse> findAllByTeamIdWithPagination(
            Long teamId, Long lastProjectId, int pageSize, TeamParticipant teamParticipant, boolean isParticipating) {

        List<ProjectInfoResponse> responses =
                queryFactory
                        .select(qProject)
                        .from(qProject)
                        .where(qProject.team.id.eq(teamId), participantCondition(isParticipating, teamParticipant), lastProjectCondition(lastProjectId))
                        .orderBy(qProject.id.desc())
                        .limit(pageSize + 1)
                        .fetch()
                        .stream()
                        .map(
							ProjectInfoResponse::from)
                        .collect(Collectors.toList());

        return checkLastPage(pageSize, responses);
    }
	private BooleanExpression participantCondition(boolean isParticipant, TeamParticipant teamParticipant) {
		if (isParticipant) {
			return qProjectParticipant.teamParticipant.eq(teamParticipant);
		} else {
			return qProjectParticipant.teamParticipant.isNull();
		}
	}


    private BooleanExpression lastProjectCondition(Long projectId) {
        return (projectId == null) ? null : qProject.id.lt(projectId);
    }

    private Slice<ProjectInfoResponse> checkLastPage(
            int pageSize, List<ProjectInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }

    private boolean projectParticipantExists(Project project, TeamParticipant teamParticipant) {
        return queryFactory
                        .selectOne()
                        .from(qProjectParticipant)
                        .where(
                                qProjectParticipant
                                        .project
                                        .eq(project)
                                        .and(
                                                qProjectParticipant.teamParticipant.eq(
                                                        teamParticipant)))
                        .fetchFirst()
                != null;
    }
}
