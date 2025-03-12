package com.amcamp.infra.config.feign;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.amcamp.domain.feedback.dto.request.ChatRequest;
import com.amcamp.domain.feedback.dto.response.ChatResponse;
import com.amcamp.global.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "openAiClient",
        url = "https://api.openai.com",
        configuration = FeignConfig.class)
public interface OpenAiClient {
    @PostMapping(value = "/v1/chat/completions", consumes = APPLICATION_JSON_VALUE)
    ChatResponse getAiFeedback(
            @RequestHeader("Authorization") String apiKey, @RequestBody ChatRequest request);
}
