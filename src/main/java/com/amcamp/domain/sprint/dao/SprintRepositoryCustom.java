package com.amcamp.domain.sprint.dao;

import com.amcamp.domain.sprint.dto.response.SprintInfoResponse;
import org.springframework.data.domain.Slice;

public interface SprintRepositoryCustom {
    Slice<SprintInfoResponse> findAllSprintByProjectId(Long projectId, Long lastSprintId);
}
