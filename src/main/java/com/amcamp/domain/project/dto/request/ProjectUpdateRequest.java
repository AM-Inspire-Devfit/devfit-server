package com.amcamp.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ProjectUpdateRequest(
        @Schema(description = "수정된 텍스트", example = "new project title") String title,
        @Schema(description = "수정된 텍스트", example = "new project description") String description,
        @Schema(description = "수정된 프로젝트 마감일자", example = "2026-04-01") LocalDate dueDt) {}
