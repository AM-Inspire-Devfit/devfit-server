package com.amcamp.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CommonResponse <T> {
	private boolean success;
	private int status;
	private T data;
	private LocalDateTime timestamp;

	public static <T> CommonResponse<T> onSuccess(int status, T data) {
		return CommonResponse.<T>builder()
			.success(true)
			.status(status)
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}
	public static <T> CommonResponse<T> onFailure(int status, T data) {
		return CommonResponse.<T>builder()
			.success(false)
			.status(status)
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}


}
