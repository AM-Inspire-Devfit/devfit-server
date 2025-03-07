package com.amcamp.domain.task.dao;

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
                        .where(
                                task.sprint.project.id.eq(projectId),
                                task.assignee.eq(assignee),
                                task.sprint.id.eq(lastSprintId))
                        .orderBy(task.createdDt.asc())
                        .limit(pageSize + 1)
                        .fetch();

        return checkLastPage(pageSize, results);
    }

    private BooleanExpression lastSprintId(Long sprintId) {
        if (sprintId == null) {
            return null;
        }
        return sprint.id.lt(sprintId);
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
