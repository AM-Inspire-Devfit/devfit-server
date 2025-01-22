package com.amcamp.domain.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@GetMapping("/test-success")
	public TestResponse testSuccess() {
		return new TestResponse(1L, "am");
	}

}

