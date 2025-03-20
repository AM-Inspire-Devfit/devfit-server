package com.amcamp.domain.project.dao;

import com.amcamp.domain.project.dto.response.ProjectRegistrationInfoResponse;
import org.springframework.data.domain.Slice;

public interface ProjectRegistrationRepositoryCustom {
    Slice<ProjectRegistrationInfoResponse> findAllByProjectIdWithPagination(
            Long projectId, Long lastRegistrationId, int pageSize);
}
