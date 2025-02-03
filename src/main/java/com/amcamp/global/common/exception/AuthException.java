package com.amcamp.global.common.exception;

import com.amcamp.global.common.exception.errorcode.BaseErrorCode;
import lombok.Getter;


@Getter
public class AuthException extends CommonException {

	public AuthException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
