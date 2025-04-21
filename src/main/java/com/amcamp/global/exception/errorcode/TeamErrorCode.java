package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamErrorCode implements BaseErrorCode {
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 팀을 찾을 수 없습니다."),
    MEMBER_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "이미 이 팀에 참가한 회원입니다."),
    TEAM_PARTICIPANT_REQUIRED(HttpStatus.FORBIDDEN, "팀 참여자가 아닙니다."),
    INVALID_INVITE_CODE(HttpStatus.NOT_FOUND, "팀이 존재하지 않거나 코드가 만료되었습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "수정 권한이 없습니다."),

    TEAM_NOT_EXISTS(HttpStatus.NOT_FOUND, "회원이 참여한 팀이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
