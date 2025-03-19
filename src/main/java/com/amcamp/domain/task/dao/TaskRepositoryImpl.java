package com.amcamp.domain.task.dao;

import static com.amcamp.domain.task.domain.QTask.task;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.task.domain.AssignedStatus;
import com.amcamp.domain.task.domain.QTask;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<TaskInfoResponse> findBySprint(Long sprintId, Long lastTaskId, int size) {
        List<TaskInfoResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        TaskInfoResponse.class,
                                        task.id,
                                        task.description,
                                        task.taskDifficulty,
                                        task.toDoInfo.startDt,
                                        task.toDoInfo.dueDt,
                                        task.toDoInfo.toDoStatus,
                                        task.assignedStatus,
                                        task.sosStatus,
                                        getAssigneeId(task),
                                        getAssigneeNickname(task),
                                        getAssigneeProfile(task)))
                        .from(task)
                        .leftJoin(task.assignee)
                        .where(task.sprint.id.eq(sprintId), lastTaskId(lastTaskId))
                        .orderBy(task.createdDt.asc())
                        .limit(size + 1)
                        .fetch();

        System.out.println("조회된 Task 개수: " + results.size());
        return checkLastPage(size, results);
    }

    @Override
    public Slice<TaskInfoResponse> findBySprintAndAssignee(
            Long sprintId, ProjectParticipant assignee, Long lastTaskId, int size) {
        List<TaskInfoResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        TaskInfoResponse.class,
                                        task.id,
                                        task.description,
                                        task.taskDifficulty,
                                        task.toDoInfo.startDt,
                                        task.toDoInfo.dueDt,
                                        task.toDoInfo.toDoStatus,
                                        task.assignedStatus,
                                        task.sosStatus,
                                        task.assignee.id,
                                        task.assignee.projectNickname,
                                        task.assignee.projectProfile))
                        .from(task)
                        .where(
                                task.sprint.id.eq(sprintId),
                                lastTaskId(lastTaskId),
                                task.assignee.eq(assignee))
                        .orderBy(task.createdDt.asc())
                        .limit(size + 1)
                        .fetch();

        return checkLastPage(size, results);
    }

    public Expression<Long> getAssigneeId(QTask task) {
        return new CaseBuilder()
                .when(task.assignedStatus.eq(AssignedStatus.ASSIGNED))
                .then(task.assignee.id)
                .otherwise(Expressions.nullExpression());
    }

    public Expression<String> getAssigneeNickname(QTask task) {
        return new CaseBuilder()
                .when(task.assignedStatus.eq(AssignedStatus.ASSIGNED))
                .then(task.assignee.projectNickname)
                .otherwise(Expressions.nullExpression());
    }

    private Expression<String> getAssigneeProfile(QTask task) {
        return new CaseBuilder()
                .when(task.assignedStatus.eq(AssignedStatus.ASSIGNED))
                .then(task.assignee.projectProfile)
                .otherwise(Expressions.nullExpression());
    }

    private BooleanExpression lastTaskId(Long taskId) {
        if (taskId == null) {
            return null;
        }
        return task.id.gt(taskId);
    }

    private Slice<TaskInfoResponse> checkLastPage(int pageSize, List<TaskInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}
