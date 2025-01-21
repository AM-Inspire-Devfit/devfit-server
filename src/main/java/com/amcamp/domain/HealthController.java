package com.amcamp.domain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "애플리케이션 상태 확인 API")
@RestController
public class HealthController {

	@Operation(summary = "애플리케이션 헬스 체크", description = "서버 상태를 확인하고 정상 동작 여부를 확인하는 API")
	@GetMapping("/health_check")
	public String healthCheck() {
		return "good";
	}
}
