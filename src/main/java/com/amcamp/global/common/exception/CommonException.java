package com.amcamp.global.common.exception;

import com.amcamp.global.common.exception.errorcode.BaseErrorCode;
import lombok.Getter;


@Getter
public class CommonException extends RuntimeException {

	private BaseErrorCode errorCode;
	private String errorField;
	private String errorGiven;

	public CommonException(BaseErrorCode errorCode) {
		super(errorCode.getErrorMsg().getReason());
		this.errorCode = errorCode;
	}

	public CommonException(BaseErrorCode errorCode, String errorField, String errorGiven) {
		super(errorCode.getErrorMsg().getReason()); //RuntimeExceptio
		this.errorCode = errorCode;
		this.errorField = errorField;
		this.errorGiven = errorGiven;
	}
}
