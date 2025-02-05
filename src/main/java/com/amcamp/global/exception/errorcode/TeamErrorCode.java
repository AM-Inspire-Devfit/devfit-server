package com.amcamp.global.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum TeamErrorCode  implements BaseErrorCode{
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 팀을 찾을 수 없습니다."),
	ALREADY_PARTICIPANT(HttpStatus.BAD_REQUEST, "이미 이 팀에 참가한 회원입니다."),
	TEAM_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "팀 참여자가 아닙니다.")
	;

	private final HttpStatus httpStatus;
	private final String message;

	TeamErrorCode(HttpStatus httpStatus, String message){
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getCodeName() {
		return this.name();
	}
}
