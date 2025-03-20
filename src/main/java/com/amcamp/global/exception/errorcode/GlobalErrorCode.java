package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다. 관리자에게 문의해주세요."),
    INVALID_DATE_ERROR(HttpStatus.BAD_REQUEST, "마감일은 현재 날짜 이후여야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
