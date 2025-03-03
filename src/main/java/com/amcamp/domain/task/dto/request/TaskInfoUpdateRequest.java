package com.amcamp.domain.task.dto.request;

import com.amcamp.domain.task.domain.TaskDifficulty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskInfoUpdateRequest(
        @NotBlank(message = "태스크 내용은 필수값입니다.")
                @Schema(description = "태스크 내용", example = "피그마 화면 설계 수정")
                String description,
        @NotBlank(message = "태스크 난이도는 필수값입니다.") @Schema(description = "태스크 난이도", example = "중")
                TaskDifficulty taskDifficulty,
        @NotNull(message = "태스크 시작 날짜는 비워둘 수 없습니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                @Schema(description = "태스크 시작 날짜", defaultValue = "2026-02-01")
                LocalDate startDt,
        @NotNull(message = "태스크 마감 날짜는 비워둘 수 없습니다.")
                @JsonFormat(
                        shape = JsonFormat.Shape.STRING,
                        pattern = "yyyy-MM-dd",
                        timezone = "Asia/Seoul")
                @Schema(description = "태스크 마감 날짜", defaultValue = "2026-03-01")
                LocalDate dueDt) {}
