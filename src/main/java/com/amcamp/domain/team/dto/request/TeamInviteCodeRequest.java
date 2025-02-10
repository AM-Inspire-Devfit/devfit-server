package com.amcamp.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TeamInviteCodeRequest(
        @NotEmpty
                @Size(min = 8, max = 8, message = "8자리 초대 코드를 입력해주세요.")
                @Schema(description = "팀 코드", example = "FFbKeJvJ")
                String inviteCode) {}
