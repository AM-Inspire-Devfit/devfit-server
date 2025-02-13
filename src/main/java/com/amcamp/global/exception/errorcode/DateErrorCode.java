package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DateErrorCode implements BaseErrorCode {
    INVALID_DATE_PERIOD_ERROR(HttpStatus.BAD_REQUEST, "시작 날짜는 마감 날짜보다 늦을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    DateErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCodeName() {
        return this.name();
    }
}
