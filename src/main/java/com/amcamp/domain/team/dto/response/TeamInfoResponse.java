package com.amcamp.domain.team.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TeamInfoResponse(
	@NotEmpty(message = "팀 ID는 필수 응답 사항입니다.")
	Long teamId,

	@NotEmpty(message = "팀 이름 필수 응답 사항입니다.")
	@Size(max = 25, message = "팀 이름은 최대 25자까지 입력 가능합니다.")
	String teamName) {
}
