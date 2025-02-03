package com.amcamp.global.common.exception;

import com.amcamp.global.common.CommonResponse;
import com.amcamp.global.common.exception.errorcode.BaseErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.amcamp")
public class GlobalExceptionManager {
	@ExceptionHandler(AuthException.class)
	public CommonResponse<?> commonExceptionHandler(CommonException e){
		BaseErrorCode baseErrorCode = e.getErrorCode();
		ErrorDetail errorDetail = ErrorDetail.builder()
			.field(e.getErrorField())
			.given(e.getErrorGiven())
			.reasonMessage(baseErrorCode.getErrorMsg())
			.build();
		return CommonResponse.onFailure(baseErrorCode.getHttpStatus().value(),errorDetail);
	}
}
