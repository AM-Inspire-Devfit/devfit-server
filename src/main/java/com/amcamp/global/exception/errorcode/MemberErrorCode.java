package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

    MEMBER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 탈퇴한 회원입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
