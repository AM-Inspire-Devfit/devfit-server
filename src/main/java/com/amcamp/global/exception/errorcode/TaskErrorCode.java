package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TaskErrorCode implements BaseErrorCode {
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "task를 찾을 수 없습니다."),
    TASK_MODIFY_FORBIDDEN(HttpStatus.FORBIDDEN, "task 수정·삭제 권한이 없습니다. "),
    TASK_ALREADY_ASSIGNED(HttpStatus.FORBIDDEN, "이미 담당자가 존재하는 task입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
