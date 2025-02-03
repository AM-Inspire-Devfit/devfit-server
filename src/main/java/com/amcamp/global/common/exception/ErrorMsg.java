package com.amcamp.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMsg {
	private Integer status;
	private String code;
	private String reason;

	@Override
	public String toString() {
		return String.format("ErrorMsg{code='%s', status=%d, reason='%s'}", code, status, reason);
	}
}
