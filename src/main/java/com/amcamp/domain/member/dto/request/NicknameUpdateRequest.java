package com.amcamp.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NicknameUpdateRequest(
	@NotNull(message = "닉네임은 비워둘 수 없습니다.")
	@Schema(description = "닉네임", example = "최현태")
	String nickname) {
}
