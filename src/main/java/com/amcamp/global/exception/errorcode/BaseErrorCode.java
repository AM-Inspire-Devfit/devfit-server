package com.amcamp.global.exception.errorcode;

import com.amcamp.global.exception.ErrorMsg;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
	HttpStatus getHttpStatus();
	String getMessage();
	String getCodeName();

	// 공통 ErrorMsg 생성 메서드
	default ErrorMsg getErrorMsg(){
		return ErrorMsg.builder()
			.code(getCodeName()) // Enum 이름을 사용
			.reason(getMessage()) // 오류 메시지
			.build();
	}

}
