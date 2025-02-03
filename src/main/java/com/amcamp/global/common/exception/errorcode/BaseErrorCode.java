package com.amcamp.global.common.exception.errorcode;

import com.amcamp.global.common.exception.ErrorMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
	HttpStatus getHttpStatus();
	String getMessage();

	String getCodeName();

	// 공통 ErrorMsg 생성 메서드
	default ErrorMsg getErrorMsg(){
		return ErrorMsg.builder()
			.code(getCodeName()) // Enum 이름을 사용
			.status(getHttpStatus().value()) // HttpStatus의 숫자 값
			.reason(getMessage()) // 오류 메시지
			.build();
	}

}
