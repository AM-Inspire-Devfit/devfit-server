package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FeedbackErrorCode implements BaseErrorCode {
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백을 받을 프로젝트 참여자를 찾을 수 없습니다."),

    INVALID_PROJECT_PARTICIPANT(HttpStatus.BAD_REQUEST, "같은 프로젝트에 속한 사람에게만 피드백을 보낼 수 있습니다."),
    CANNOT_SEND_FEEDBACK_TO_SELF(HttpStatus.BAD_REQUEST, "본인에게 피드백을 보낼 수 없습니다."),

    FEEDBACK_ALREADY_SENT(HttpStatus.BAD_REQUEST, "이미 해당 사용자에게 피드백을 보냈습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
