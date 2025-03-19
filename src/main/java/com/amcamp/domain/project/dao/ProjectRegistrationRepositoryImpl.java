package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.domain.QProject;
import com.amcamp.domain.project.domain.QProjectRegistration;
import com.amcamp.domain.project.dto.response.ProjectRegistrationInfoResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ProjectRegistrationRepositoryImpl implements ProjectRegistrationRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QProject qProject = QProject.project;
    private final QProjectRegistration qProjectRegistration =
            QProjectRegistration.projectRegistration;

    @Override
    public Slice<ProjectRegistrationInfoResponse> findAllByProjectIdWithPagination(
            Long teamId, Long lastProjectId, int pageSize) {

        List<ProjectRegistrationInfoResponse> responses =
                queryFactory
                        .select(qProjectRegistration)
                        .from(qProjectRegistration)
                        .leftJoin(qProject)
                        .on(qProjectRegistration.project.eq(qProject))
                        .where(qProject.team.id.eq(teamId), lastProjectCondition(lastProjectId))
                        .orderBy(qProject.id.desc())
                        .limit(pageSize + 1)
                        .fetch()
                        .stream()
                        .map(ProjectRegistrationInfoResponse::from)
                        .collect(Collectors.toList());

        return checkLastPage(pageSize, responses);
    }

    private BooleanExpression lastProjectCondition(Long projectId) {
        return (projectId == null) ? null : qProject.id.lt(projectId);
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
