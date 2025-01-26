package com.amcamp.global.common.exception;

import com.amcamp.global.common.CommonResponse;
import com.amcamp.global.common.exception.auth.AuthException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.amcamp")//추후 모듈 별로 나누어 적용
public class GlobalExceptionManager {
	@ExceptionHandler(AuthException.class)
	public CommonResponse<?> AuthExceptionHandler(AuthException e){
		ErrorDetail errorDetail = ErrorDetail.builder()
			.field(e.getErrorField())
			.given(e.getErrorGiven())
			.reasonMessage(e.getAuthErrorCode().name()+": "+e.getMessage())
			.build();
		return CommonResponse.onFailure(e.getAuthErrorCode().getHttpStatusValue(),errorDetail);
	}
}
