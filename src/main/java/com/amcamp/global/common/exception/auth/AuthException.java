package com.amcamp.global.common.exception.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException{

	private final AuthErrorCode authErrorCode;
	private final String message;
	private String errorField;
	private String errorGiven;

	public AuthException(AuthErrorCode authErrorCode, String message){
		this.authErrorCode = authErrorCode;
		this.message = message;
	}

}
