package com.amcamp.domain.task.dto.request;

import com.amcamp.domain.task.domain.TaskDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateRequest(
        @NotNull(message = "스프린트 ID는 필수값입니다.") @Schema(description = "스프린트 ID", example = "1")
                Long sprintId,
        @NotBlank(message = "태스크 내용은 필수값입니다.")
                @Schema(description = "태스크 내용", example = "피그마 화면 설계 수정")
                String description,
        @NotNull(message = "태스크 난이도는 필수값입니다.") @Schema(description = "태스크 난이도", example = "MID")
                TaskDifficulty taskDifficulty) {}
