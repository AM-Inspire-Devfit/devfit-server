package com.amcamp.domain.feedback.api;

import com.amcamp.domain.feedback.application.FeedbackService;
import com.amcamp.domain.feedback.dto.request.OriginalFeedbackRequest;
import com.amcamp.domain.feedback.dto.response.FeedbackRefineResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "7. 피드백 API", description = "피드백 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
            summary = "OpenAI 기반 피드백 메시지 개선",
            description = "사용자가 입력한 피드백을 AI가 분석하여 부드럽고 명확하게 개선합니다.")
    @PostMapping(value = "/refinement")
    public FeedbackRefineResponse feedbackRefine(
            @Valid @RequestBody OriginalFeedbackRequest request) {
        return feedbackService.refineFeedback(request);
    }
}
