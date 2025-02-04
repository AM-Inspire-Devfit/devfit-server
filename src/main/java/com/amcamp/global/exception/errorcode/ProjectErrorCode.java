package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProjectErrorCode implements BaseErrorCode {

	PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청한 project 를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ProjectErrorCode(HttpStatus httpStatus, String message){
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getCodeName() {
		return this.name();
	}

}
