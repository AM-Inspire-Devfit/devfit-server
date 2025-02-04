package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements BaseErrorCode {

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String message;

	MemberErrorCode(HttpStatus httpStatus, String message){
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getCodeName() {
		return this.name();
	}
}
