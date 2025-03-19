package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.dto.response.ProjectRegistrationInfoResponse;
import org.springframework.data.domain.Slice;

public interface ProjectRegistrationRepositoryCustom {
    Slice<ProjectRegistrationInfoResponse> findAllByProjectIdWithPagination(
            Long teamId, Long lastProjectId, int pageSize);
}
