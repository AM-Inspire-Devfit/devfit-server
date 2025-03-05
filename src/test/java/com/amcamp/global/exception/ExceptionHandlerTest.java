package com.amcamp.global.exception;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.amcamp.global.common.response.CommonResponse;
import com.amcamp.global.exception.errorcode.AuthErrorCode;
import com.amcamp.global.exception.errorcode.ProjectErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExceptionHandlerTest {

    AuthErrorCode authErrorCode = AuthErrorCode.ID_TOKEN_VERIFICATION_FAILED;
    ProjectErrorCode projectErrorCode = ProjectErrorCode.PROJECT_NOT_FOUND;

    void throwExceptionWithAuthErrorCode(Object o) {
        Optional<Object> mockObject = Optional.ofNullable(o);
        if (!mockObject.isPresent()) {
            throw new CommonException(authErrorCode);
        }
    }

    void throwExceptionWithProjectErrorCode(Object o) {
        Optional<Object> mockObject = Optional.ofNullable(o);
        if (!mockObject.isPresent()) {
            throw new CommonException(projectErrorCode);
        }
    }

    @Test
    @DisplayName("authError Test")
    void commonExceptionWithAuthErrorTest() {
        assertThatThrownBy(() -> throwExceptionWithAuthErrorCode(null))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(authErrorCode.getMessage());
    }

    @Test
    @DisplayName("projectError Test")
    void commonExceptionWithProjectErrorTest() {
        assertThatThrownBy(() -> throwExceptionWithProjectErrorCode(null))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(projectErrorCode.getMessage());
    }

    @Test
    @DisplayName("ExceptionHandler Test")
    void globalExceptionHandlerTest() {

        // given: 예외 핸들러와 예외 생성
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        CommonException exception = new CommonException(ProjectErrorCode.PROJECT_NOT_FOUND);

        // when: 예외 핸들러 실행
        CommonResponse<?> response =
                globalExceptionHandler.handleCustomException(exception).getBody();

        // then: 응답 객체 검증
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(400);

        // ErrorResponse 객체 검증
        ErrorResponse errorResponse = (ErrorResponse) response.getData();
        assertThat(errorResponse.errorClassName())
                .isEqualTo(ProjectErrorCode.PROJECT_NOT_FOUND.name());
        assertThat(errorResponse.message())
                .isEqualTo(ProjectErrorCode.PROJECT_NOT_FOUND.getMessage());
    }
}
