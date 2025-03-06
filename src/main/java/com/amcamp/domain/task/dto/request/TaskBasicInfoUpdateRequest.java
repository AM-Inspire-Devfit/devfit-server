package com.amcamp.domain.task.dto.request;

import com.amcamp.domain.task.domain.TaskDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;

public record TaskBasicInfoUpdateRequest(
        @Schema(description = "태스크 내용", example = "피그마 화면 설계 1차 수정") String description,
        @Schema(description = "태스크 난이도", example = "HIGH") TaskDifficulty taskDifficulty) {}
