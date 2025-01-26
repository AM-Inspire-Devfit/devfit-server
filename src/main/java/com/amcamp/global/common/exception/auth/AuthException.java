package com.amcamp.global.common.exception.auth;

import com.amcamp.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException{

	private final ErrorCode errorCode;
	private final String message;
	private String errorField;
	private String errorGiven;

	public AuthException(ErrorCode errorCode, String message){
		this.errorCode = errorCode;
		this.message = message;
	}

}
