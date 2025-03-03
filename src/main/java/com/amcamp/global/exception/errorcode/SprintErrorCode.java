package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SprintErrorCode implements BaseErrorCode {
    SPRINT_NOT_FOUND(HttpStatus.BAD_REQUEST, "스프린트를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    SprintErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCodeName() {
        return this.name();
    }
}
