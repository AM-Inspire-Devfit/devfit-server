package com.amcamp.global.exception.errorcode;

import com.amcamp.global.exception.CommonException;
import lombok.Getter;


@Getter
public class AuthException extends CommonException {

	public AuthException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
