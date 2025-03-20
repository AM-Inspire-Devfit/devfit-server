package com.amcamp.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectCreateRequest(
        @Schema(description = "팀 ID", example = "1") @NotNull Long teamId,
        @Schema(description = "프로젝트 제목", example = "Devfit") @NotBlank String projectTitle,
        @Schema(description = "프로젝트 마감 날짜", example = "2027-01-01")
                @JsonFormat(shape = JsonFormat.Shape.STRING)
                @NotNull
                LocalDate dueDt,
        @Schema(description = "프로젝트 설명", example = "LG CNS AM Inspire Camp 사이드 프로젝트")
                String projectDescription) {}
