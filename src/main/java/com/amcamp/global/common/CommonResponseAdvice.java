package com.amcamp.global.common;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
@RestControllerAdvice(basePackages = "com.amcamp")
public class CommonResponseAdvice implements ResponseBodyAdvice {
	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {
		return true; //어떤 응답을 가로채서 반환할 것인지 -> 모든 응답 가로채기
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
		int status = httpServletResponse.getStatus();
		HttpStatus resolve = HttpStatus.resolve(status);

		if (resolve == null || body instanceof String) {
			return body;
		}
		if (resolve.is2xxSuccessful()) {
			return CommonResponse.onSuccess(status, body);
		}
		return body;
	}
}
