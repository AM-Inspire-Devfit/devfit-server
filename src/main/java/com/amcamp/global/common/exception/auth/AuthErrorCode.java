package com.amcamp.global.common.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {

	ID_TOKEN_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "ID 토큰 검증에 실패했습니다."),
	;

	//잘못된 요청
//	WRONG_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),

	//Member 요청 관련 에러
//	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 USER 정보를 찾을 수 없습니다"),
//	USERNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 USER 입니다"),
//	NO_AUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증되지 않은 USER 입니다"),

	//Token 유효성 Error
//	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
//	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "요청한 토큰을 찾을 수 없습니다"),

	//Role 권한 관련 오류
//	NO_AUTHORITY(HttpStatus.BAD_REQUEST, "권한 정보를 찾을 수 없습니다"),
//	INVALID_AUTHORITY(HttpStatus.BAD_REQUEST, "접근 권한이 없습니다");

	private final HttpStatus httpStatus;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, String message){
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public int getHttpStatusValue(){
		return httpStatus.value();
	}
}
