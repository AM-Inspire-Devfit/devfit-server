package com.amcamp.global.exception;

import com.amcamp.global.common.response.CommonResponse;
import com.amcamp.global.exception.errorcode.BaseErrorCode;
import com.amcamp.global.exception.errorcode.GlobalErrorCode;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "com.amcamp")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResponse<ErrorDetail>> commonExceptionHandler(CommonException e) {
        final BaseErrorCode errorCode = e.getErrorCode();
        final ErrorDetail errorDetail =
                ErrorDetail.builder()
                        .field(e.getErrorField())
                        .given(e.getErrorGiven())
                        .reasonMessage(errorCode.getErrorMsg())
                        .build();
        final CommonResponse<ErrorDetail> response =
                CommonResponse.onFailure(errorCode.getHttpStatus().value(), errorDetail);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @SneakyThrows
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        final String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        final ErrorResponse errorResponse =
                ErrorResponse.of(e.getClass().getSimpleName(), errorMessage);
        final CommonResponse<ErrorResponse> response =
                CommonResponse.onFailure(HttpStatus.BAD_REQUEST.value(), errorResponse);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse> handleException(Exception e) {
        final BaseErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        final ErrorResponse errorResponse =
                ErrorResponse.of(e.getClass().getSimpleName(), errorCode.getMessage());
        final CommonResponse response =
                CommonResponse.onFailure(errorCode.getHttpStatus().value(), errorResponse);

        return ResponseEntity.status(errorCode.getHttpStatus().value()).body(response);
    }
}
