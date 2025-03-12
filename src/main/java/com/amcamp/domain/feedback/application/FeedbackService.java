package com.amcamp.domain.feedback.application;

import com.amcamp.domain.feedback.dto.request.OriginalFeedbackRequest;
import com.amcamp.domain.feedback.dto.response.FeedbackRefineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final ChatGptService chatGptService;

    public FeedbackRefineResponse refineFeedback(OriginalFeedbackRequest request) {
        String chatResponse = chatGptService.getAiFeedback(request.originalMessage());
        return new FeedbackRefineResponse(chatResponse);
    }
}
