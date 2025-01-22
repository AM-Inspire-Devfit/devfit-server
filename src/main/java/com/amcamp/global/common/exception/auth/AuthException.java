package com.amcamp.global.common.exception.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException{

	private final ErrorCode errorCode;
	private final String message;

}
