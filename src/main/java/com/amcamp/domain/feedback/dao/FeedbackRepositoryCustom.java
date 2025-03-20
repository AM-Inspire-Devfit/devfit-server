package com.amcamp.domain.feedback.dao;

import com.amcamp.domain.feedback.dto.response.FeedbackInfoResponse;
import org.springframework.data.domain.Slice;

public interface FeedbackRepositoryCustom {
    Slice<FeedbackInfoResponse> findSprintFeedbacksByParticipant(
            Long projectParticipantId, Long sprintId, Long lastFeedbackId, int pageSize);
}
