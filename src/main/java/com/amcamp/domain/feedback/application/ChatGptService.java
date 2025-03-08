package com.amcamp.domain.feedback.application;

import static com.amcamp.global.common.constants.SecurityConstants.TOKEN_PREFIX;

import com.amcamp.domain.feedback.dto.request.ChatRequest;
import com.amcamp.domain.feedback.dto.response.ChatResponse;
import com.amcamp.infra.config.feign.OpenAiClient;
import com.amcamp.infra.config.openai.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final OpenAiProperties openAiProperties;
    private final OpenAiClient openAiClient;

    public String getAiFeedback(String userMessage) {
        String apiKey = TOKEN_PREFIX + openAiProperties.apiKey();
        ChatRequest request = ChatRequest.of(openAiProperties.model(), userMessage);

        ChatResponse response = openAiClient.getAiFeedback(apiKey, request);
        return response.choices()[0].message().content();
    }
}
