package com.amcamp.domain.sprint.dao;

import static com.amcamp.domain.sprint.domain.QSprint.sprint;

import com.amcamp.domain.sprint.dto.response.SprintInfoResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SprintRepositoryImpl implements SprintRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<SprintInfoResponse> findAllSprintByProjectId(Long projectId, Long lastSprintId) {
        List<SprintInfoResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        SprintInfoResponse.class,
                                        sprint.id,
                                        sprint.title,
                                        sprint.goal,
                                        sprint.toDoInfo.startDt,
                                        sprint.toDoInfo.dueDt,
                                        sprint.toDoInfo.toDoStatus,
                                        sprint.progress.intValue()))
                        .from(sprint)
                        .where(lastSprintId(lastSprintId), sprint.project.id.eq(projectId))
                        .orderBy(sprint.title.asc())
                        .limit(2)
                        .fetch();

        if (results.isEmpty()) {
            throw new CommonException(SprintErrorCode.SPRINT_NOT_FOUND);
        }

        return checkLastPage(results);
    }

    private BooleanExpression lastSprintId(Long sprintId) {
        if (sprintId == null) {
            return null;
        }

        return sprint.id.gt(sprintId);
    }

    private Slice<SprintInfoResponse> checkLastPage(List<SprintInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > 1) {
            hasNext = true;
            results.remove(1);
        }

        return new SliceImpl<>(results, PageRequest.of(0, 1), hasNext);
    }
}
