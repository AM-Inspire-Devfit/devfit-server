package com.amcamp.domain.task.dao;

import com.amcamp.domain.project.domain.ProjectParticipant;
import com.amcamp.domain.task.dto.response.TaskInfoResponse;
import org.springframework.data.domain.Slice;

public interface TaskRepositoryCustom {
    Slice<TaskInfoResponse> findTasksByProject(Long projectId, Long lastSprintId, int pageSize);

    Slice<TaskInfoResponse> findTasksByMember(
            Long projectId, Long lastSprintId, ProjectParticipant projectParticipant, int pageSize);
}
