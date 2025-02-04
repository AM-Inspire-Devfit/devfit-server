package com.amcamp.global.exception;

import com.amcamp.global.common.response.CommonResponse;
import com.amcamp.global.exception.errorcode.BaseErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "com.amcamp")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CommonException.class)
	public ResponseEntity<CommonResponse<ErrorDetail>> commonExceptionHandler(CommonException e) {
		final BaseErrorCode errorCode = e.getErrorCode();
		final ErrorDetail errorDetail = ErrorDetail.builder()
			.field(e.getErrorField())
			.given(e.getErrorGiven())
			.reasonMessage(errorCode.getErrorMsg())
			.build();
		final CommonResponse<ErrorDetail> response =
			CommonResponse.onFailure(errorCode.getHttpStatus().value(), errorDetail);

		return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
	}
}
