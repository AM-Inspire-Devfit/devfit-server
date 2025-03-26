package com.amcamp.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SprintErrorCode implements BaseErrorCode {
    SPRINT_NOT_FOUND(HttpStatus.NOT_FOUND, "스프린트를 찾을 수 없습니다."),
    SPRINT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "스프린트 삭제 권한이 없습니다."),

    TASK_NOT_CREATED_YET(HttpStatus.NOT_FOUND, "스프린트 내 태스크가 존재하지 않습니다."),

    SPRINT_DUE_DATE_INVALID(HttpStatus.BAD_REQUEST, "스프린트 마감일은 프로젝트 마감일 이내여야 합니다."),
    PREVIOUS_SPRINT_NOT_ENDED(HttpStatus.BAD_REQUEST, "이전 스프린트가 아직 종료되지 않았습니다."),
    NEXT_SPRINT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "다음 스프린트가 존재할 경우, 마감일은 그 이전이어야 합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
