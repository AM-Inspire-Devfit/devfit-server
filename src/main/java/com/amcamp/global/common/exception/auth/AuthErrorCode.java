package com.amcamp.global.common.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {

	//잘못된 요청
	WRONG_REQUEST(HttpStatus.BAD_REQUEST),

	//Member 요청 관련 에러
	USER_NOT_FOUND(HttpStatus.NOT_FOUND),
	USERNAME_DUPLICATED(HttpStatus.CONFLICT),
	NO_AUTHORIZED_USER(HttpStatus.UNAUTHORIZED),

	//Token 유효성 Error
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED),

	//Role 권한 관련 오류
	NO_AUTHORITY(HttpStatus.BAD_REQUEST),
	INVALID_AUTHORITY(HttpStatus.BAD_REQUEST);


	private final HttpStatus httpStatus;
	AuthErrorCode(HttpStatus httpStatus){
		this.httpStatus = httpStatus;
	}

	public int getHttpStatusValue(){
		return httpStatus.value();
	}


}
