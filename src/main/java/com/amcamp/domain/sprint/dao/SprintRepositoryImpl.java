package com.amcamp.domain.sprint.dao;

import static com.amcamp.domain.sprint.domain.QSprint.sprint;
import static com.amcamp.domain.task.domain.QTask.task;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.sprint.dto.response.SprintDetailResponse;
import com.amcamp.domain.task.dto.response.TaskBasicInfoResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.SprintErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
    public Slice<SprintDetailResponse> findAllSprintByProjectId(Long projectId, Long lastSprintId) {
        List<SprintDetailResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        SprintDetailResponse.class,
                                        sprint.id,
                                        sprint.title,
                                        sprint.goal,
                                        sprint.startDt,
                                        sprint.dueDt,
                                        sprint.progress.intValue(),
                                        Expressions.constant(Collections.emptyList())))
                        .from(sprint)
                        .where(lastSprintId(lastSprintId), sprint.project.id.eq(projectId))
                        .orderBy(sprint.title.asc())
                        .limit(2)
                        .fetch();

        if (results.isEmpty()) {
            throw new CommonException(SprintErrorCode.SPRINT_NOT_FOUND);
        }

        List<TaskBasicInfoResponse> taskList = fetchTaskList(results.get(0).id());

        List<SprintDetailResponse> finalResult =
                results.stream()
                        .map(
                                sprint ->
                                        new SprintDetailResponse(
                                                sprint.id(),
                                                sprint.title(),
                                                sprint.goal(),
                                                sprint.startDt(),
                                                sprint.dueDt(),
                                                sprint.progress(),
                                                taskList))
                        .collect(Collectors.toList());
        return checkLastPage(finalResult);
    }

    @Override
    public Slice<SprintDetailResponse> findAllSprintByProjectIdAndAssignee(
            Long projectId, Long lastSprintId, ProjectParticipant participant) {
        List<SprintDetailResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        SprintDetailResponse.class,
                                        sprint.id,
                                        sprint.title,
                                        sprint.goal,
                                        sprint.startDt,
                                        sprint.dueDt,
                                        sprint.progress.intValue(),
                                        Expressions.constant(Collections.emptyList())))
                        .from(sprint)
                        .where(lastSprintId(lastSprintId), sprint.project.id.eq(projectId))
                        .orderBy(sprint.title.asc())
                        .limit(2)
                        .fetch();

        if (results.isEmpty()) {
            throw new CommonException(SprintErrorCode.SPRINT_NOT_FOUND);
        }

        List<TaskBasicInfoResponse> taskList =
                fetchTaskListByAssignee(results.get(0).id(), participant);

        List<SprintDetailResponse> finalResult =
                results.stream()
                        .map(
                                sprint ->
                                        new SprintDetailResponse(
                                                sprint.id(),
                                                sprint.title(),
                                                sprint.goal(),
                                                sprint.startDt(),
                                                sprint.dueDt(),
                                                sprint.progress(),
                                                taskList))
                        .collect(Collectors.toList());
        return checkLastPage(finalResult);
    }

    private BooleanExpression lastSprintId(Long sprintId) {
        if (sprintId == null) {
            return null;
        }
        return sprint.id.gt(sprintId);
    }

    private Slice<SprintDetailResponse> checkLastPage(List<SprintDetailResponse> results) {
        boolean hasNext = false;

        if (results.size() > 1) {
            hasNext = true;
            results.remove(1);
        }

        return new SliceImpl<>(results, PageRequest.of(0, 1), hasNext);
    }

    private List<TaskBasicInfoResponse> fetchTaskList(Long sprintId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                TaskBasicInfoResponse.class,
                                task.id,
                                task.description,
                                task.taskStatus,
                                task.sosStatus))
                .from(task)
                .where(task.sprint.id.eq(sprintId))
                .orderBy(task.id.asc())
                .fetch();
    }

    private List<TaskBasicInfoResponse> fetchTaskListByAssignee(
            Long sprintId, ProjectParticipant participant) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                TaskBasicInfoResponse.class,
                                task.id,
                                task.description,
                                task.taskStatus,
                                task.sosStatus))
                .from(task)
                .where(task.sprint.id.eq(sprintId), task.assignee.eq(participant))
                .orderBy(task.id.asc())
                .fetch();
    }
}
