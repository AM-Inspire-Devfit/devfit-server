package com.amcamp.domain.task.dao;

import static com.amcamp.domain.member.domain.QMember.member;
import static com.amcamp.domain.project.domain.QProjectParticipant.projectParticipant;
import static com.amcamp.domain.sprint.domain.QSprint.sprint;
import static com.amcamp.domain.task.domain.QTask.task;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
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
public class TaskRepositoryImpl implements TaskRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<TaskInfoResponse> findTasksByProject(
            Long projectId, Long lastSprintId, int pageSize) {
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
                                        task.assignee.teamParticipant.member.id,
                                        task.assignee.teamParticipant.member.nickname,
                                        task.assignee.teamParticipant.member.profileImageUrl))
                        .from(task)
                        .leftJoin(task.sprint, sprint)
                        .on(sprint.id.eq(task.sprint.id))
                        .where(sprint.project.id.eq(projectId), lastSprintId(lastSprintId))
                        .orderBy(task.createdDt.asc())
                        .limit(pageSize + 1)
                        .fetch();

        return checkLastPage(pageSize, results);
    }

    @Override
    public Slice<TaskInfoResponse> findTasksByMember(
            Long projectId, Long lastSprintId, ProjectParticipant assignee, int pageSize) {
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
                                        task.assignee.teamParticipant.member.id,
                                        task.assignee.teamParticipant.member.nickname,
                                        task.assignee.teamParticipant.member.profileImageUrl))
                        .from(task)
                        .leftJoin(task.sprint, sprint)
                        .leftJoin(task.assignee, projectParticipant)
                        .where(
                                sprint.project.id.eq(projectId),
                                projectParticipant.eq(assignee),
                                lastSprintId(lastSprintId))
                        .orderBy(task.createdDt.asc())
                        .limit(pageSize + 1)
                        .fetch();

        return checkLastPage(pageSize, results);
    }

    private BooleanExpression lastSprintId(Long sprintId) {
        if (sprintId == null) {
            return null;
        }
        return member.id.lt(sprintId);
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
