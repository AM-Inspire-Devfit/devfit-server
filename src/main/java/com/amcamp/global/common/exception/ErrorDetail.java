package com.amcamp.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {
	private String field;
	private String given;
	private ErrorMsg reasonMessage;

}

