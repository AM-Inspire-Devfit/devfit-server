package com.amcamp.domain.team.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TeamInviteCodeRequest(
	@NotEmpty
	@Size(min = 8, max = 8, message = "8자리 초대 코드를 입력해주세요.")
	String inviteCode) {
}
