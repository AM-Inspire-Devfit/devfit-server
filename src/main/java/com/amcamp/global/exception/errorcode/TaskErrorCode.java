package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskErrorCode implements BaseErrorCode {
    TASK_NOT_FOUND(HttpStatus.BAD_REQUEST, "task를 찾을 수 없습니다."),
    TASK_MODIFY_FORBIDDEN(HttpStatus.FORBIDDEN, "task 수정·삭제 권한이 없습니다. ");

    private final HttpStatus httpStatus;
    private final String message;

    TaskErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCodeName() {
        return this.name();
    }
}
