package com.amcamp.exception;


import com.amcamp.global.common.exception.auth.AuthException;
import com.amcamp.global.common.exception.auth.AuthExceptionHandler;
import com.amcamp.global.common.exception.auth.ErrorCode;
import org.junit.jupiter.api.Test;


public class AuthExceptionHandlerTest {

	@Test
	public void userLoginExceptionTest(){
		System.out.println(
			new AuthExceptionHandler().userLoginException(
				new AuthException(ErrorCode.NO_AUTHORITY, "권한이 없는 유저입니다.")
			)
		);
	}
}
