package com.amcamp.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TeamInviteCodeRequest(
        @NotBlank @Schema(description = "팀 코드", example = "FFbKeJvJ") String inviteCode) {}
