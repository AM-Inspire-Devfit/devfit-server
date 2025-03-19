package com.amcamp.domain.project.dao;

import static com.amcamp.domain.project.domain.QProject.project;
import static com.amcamp.domain.project.domain.QProjectRegistration.projectRegistration;

import com.amcamp.domain.project.dto.response.ProjectRegistrationInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ProjectRegistrationRepositoryImpl implements ProjectRegistrationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<ProjectRegistrationInfoResponse> findAllByProjectIdWithPagination(
            Long teamId, Long lastProjectId, int pageSize) {

        List<ProjectRegistrationInfoResponse> responses =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        ProjectRegistrationInfoResponse.class,
                                        projectRegistration.project.id,
                                        projectRegistration.id,
                                        projectRegistration.requester.id,
                                        projectRegistration.requestStatus))
                        .from(projectRegistration)
                        .leftJoin(project)
                        .on(projectRegistration.project.eq(project))
                        .where(project.team.id.eq(teamId), lastProjectCondition(lastProjectId))
                        .orderBy(project.id.desc())
                        .limit(pageSize + 1)
                        .fetch();

        return checkLastPage(pageSize, responses);
    }

    private BooleanExpression lastProjectCondition(Long projectId) {
        return (projectId == null) ? null : project.id.lt(projectId);
    }

    private Slice<ProjectRegistrationInfoResponse> checkLastPage(
            int pageSize, List<ProjectRegistrationInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}
