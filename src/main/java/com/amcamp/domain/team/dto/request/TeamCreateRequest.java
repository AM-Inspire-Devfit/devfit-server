package com.amcamp.domain.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TeamCreateRequest(
	@NotEmpty(message = "팀 이름은 필수 사항입니다.")
	@Size(max = 25, message = "팀 이름은 최대 25자까지 입력 가능합니다.")
	@Schema(description = "팀 이름", example = "LG CNS AM CAMP")
	String teamName,

	@Size(max = 100, message = "팀 설명은 최대 100자까지 입력 가능합니다.")
	@Schema(description = "팀 설명", example = "LG CNS AM CAMP에서 운영하는 팀입니다.")
	String teamDescription){
}
