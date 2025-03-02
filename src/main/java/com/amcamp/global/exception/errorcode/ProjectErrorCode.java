package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProjectErrorCode implements BaseErrorCode {
    PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "project 를 찾을 수 없습니다."),

    PROJECT_PARTICIPATION_REQUIRED(HttpStatus.FORBIDDEN, "해당 프로젝트 참여자만 스프린트를 생성할 수 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ProjectErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCodeName() {
        return this.name();
    }
}
