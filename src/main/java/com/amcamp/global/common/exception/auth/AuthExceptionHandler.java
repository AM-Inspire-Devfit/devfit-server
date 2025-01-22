package com.amcamp.global.common.exception.auth;

import com.amcamp.global.common.CommonResponse;
import com.amcamp.global.common.exception.ErrorDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.amcamp")//추후 모듈 별로 나누어 적용
public class AuthExceptionHandler {
	@ExceptionHandler(AuthException.class)
	public CommonResponse<?> userLoginException(AuthException e){
		ErrorDetail errorDetail = ErrorDetail.builder()
			.field("오류가 발생한 필드 ex) username")
			.given("받은 데이터 ex)user1")
			.reasonMessage(e.getErrorCode().name()+": "+e.getMessage())
			.build();
		return CommonResponse.onFailure(e.getErrorCode().getHttpStatusValue(),errorDetail);
	}

}
