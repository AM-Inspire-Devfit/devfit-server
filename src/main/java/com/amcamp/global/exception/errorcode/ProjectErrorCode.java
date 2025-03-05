package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectErrorCode implements BaseErrorCode {
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "project 를 찾을 수 없습니다."),

    PROJECT_PARTICIPATION_REQUIRED(HttpStatus.FORBIDDEN, "해당 프로젝트 참여자가 아닙니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
