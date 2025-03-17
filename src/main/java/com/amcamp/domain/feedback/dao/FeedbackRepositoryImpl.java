package com.amcamp.domain.feedback.dao;

import static com.amcamp.domain.feedback.domain.QFeedback.feedback;

import com.amcamp.domain.feedback.dto.response.FeedbackInfoResponse;
import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.FeedbackErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<FeedbackInfoResponse> findSprintFeedbacksByParticipant(
            Long projectParticipantId, Long sprintId, Long lastFeedbackId, int pageSize) {
        List<FeedbackInfoResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        FeedbackInfoResponse.class,
                                        feedback.id,
                                        feedback.message,
                                        feedback.createdDt))
                        .from(feedback)
                        .where(
                                feedback.sprint.id.eq(sprintId),
                                feedback.receiver.id.eq(projectParticipantId),
                                lastFeedbackId(lastFeedbackId))
                        .orderBy(feedback.createdDt.desc())
                        .limit(pageSize + 1)
                        .fetch();

        if (results.isEmpty()) {
            throw new CommonException(FeedbackErrorCode.FEEDBACK_NOT_EXISTS);
        }

        return checkLastPage(pageSize, results);
    }

    private BooleanExpression lastFeedbackId(Long feedbackId) {
        if (feedbackId == null) {
            return null;
        }

        return feedback.id.lt(feedbackId);
    }

    private Slice<FeedbackInfoResponse> checkLastPage(
            int pageSize, List<FeedbackInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}
