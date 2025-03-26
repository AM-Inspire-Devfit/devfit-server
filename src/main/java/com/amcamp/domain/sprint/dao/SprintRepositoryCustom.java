package com.amcamp.domain.sprint.dao;

import com.amcamp.domain.sprint.dto.response.SprintDetailResponse;
import org.springframework.data.domain.Slice;

public interface SprintRepositoryCustom {
    Slice<SprintDetailResponse> findAllSprintByProjectId(Long projectId, Long lastSprintId);
}
