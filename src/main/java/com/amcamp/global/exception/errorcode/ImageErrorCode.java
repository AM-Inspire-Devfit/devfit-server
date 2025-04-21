package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
