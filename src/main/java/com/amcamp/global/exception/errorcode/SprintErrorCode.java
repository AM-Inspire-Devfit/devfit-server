package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SprintErrorCode implements BaseErrorCode {
    SPRINT_NOT_FOUND(HttpStatus.NOT_FOUND, "스프린트를 찾을 수 없습니다."),

    SPRINT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "스프린트 삭제 권한이 없습니다."),
    TASK_NOT_CREATED_YET(HttpStatus.BAD_REQUEST, "스프린트 내 태스크가 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
