package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MeetingErrorCode implements BaseErrorCode {
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "팀 미팅을 찾을 수 없습니다."),
    MEETING_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 동일한 이름/시간대의 미팅이 존재합니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_MEETING_DATE(HttpStatus.BAD_REQUEST, "팀 미팅은 스프린트 기간 내에 생성되어야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
