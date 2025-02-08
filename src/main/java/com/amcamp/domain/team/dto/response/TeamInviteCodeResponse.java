package com.amcamp.domain.team.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TeamInviteCodeResponse(
	@NotEmpty(message = "팀 초대코드는 필수 응답 사항입니다.")
	@Size(min = 8, max = 8, message = "8자리 초대 코드를 입력해주세요.")
	String inviteCode) {
}
