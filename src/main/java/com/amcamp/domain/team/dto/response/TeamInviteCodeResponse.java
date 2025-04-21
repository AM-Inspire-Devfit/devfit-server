package com.amcamp.domain.team.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInviteCodeResponse(
        @Schema(description = "초대 코드", example = "FFbKeJvJ") String inviteCode) {}
