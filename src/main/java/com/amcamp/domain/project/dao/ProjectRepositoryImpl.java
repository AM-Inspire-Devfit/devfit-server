package com.amcamp.domain.project.dao;

import static com.amcamp.domain.project.domain.QProject.project;
import static com.amcamp.domain.project.domain.QProjectParticipant.projectParticipant;

import com.amcamp.domain.project.dto.response.ProjectInfoResponse;
import com.amcamp.domain.team.domain.TeamParticipant;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<ProjectInfoResponse> findAllByTeamIdWithPagination(
            Long teamId,
            Long lastProjectId,
            int pageSize,
            TeamParticipant teamParticipant,
            boolean isParticipant) {

        List<ProjectInfoResponse> responses =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        ProjectInfoResponse.class,
                                        project.id,
                                        project.title,
                                        project.description,
                                        project.toDoInfo.startDt,
                                        project.toDoInfo.dueDt))
                        .from(project)
                        .leftJoin(projectParticipant)
                        .on(
                                projectParticipant
                                        .project
                                        .eq(project)
                                        .and(
                                                projectParticipant.teamParticipant.eq(
                                                        teamParticipant)))
                        .where(
                                project.team.id.eq(teamId),
                                lastProjectCondition(lastProjectId),
                                participantCondition(isParticipant, teamParticipant))
                        .orderBy(project.id.desc())
                        .limit(pageSize + 1)
                        .fetch();

        return checkLastPage(pageSize, responses);
    }

    private BooleanExpression participantCondition(
            boolean isParticipant, TeamParticipant teamParticipant) {
        if (isParticipant) {
            return projectParticipant.teamParticipant.eq(teamParticipant);
        } else {
            return projectParticipant.teamParticipant.isNull();
        }
    }

    private BooleanExpression lastProjectCondition(Long projectId) {
        return (projectId == null) ? null : project.id.lt(projectId);
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
}
