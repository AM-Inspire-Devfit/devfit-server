package com.amcamp.global.common.exception.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException{

	private final AuthErrorCode authErrorCode;
	private String errorField;
	private String errorGiven;

	// field/given null 처리용 생성자
	public AuthException(AuthErrorCode authErrorCode){
		this.authErrorCode = authErrorCode;
	}

	public String getErrorMsg(){
		return this.authErrorCode.name() + ": " + this.authErrorCode.getMessage();
	}
}
