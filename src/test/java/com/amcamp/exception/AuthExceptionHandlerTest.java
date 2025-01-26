package com.amcamp.exception;


import com.amcamp.global.common.exception.ErrorCode;
import com.amcamp.global.common.exception.GlobalExceptionManager;
import com.amcamp.global.common.exception.auth.AuthException;
import org.junit.jupiter.api.Test;

public class AuthExceptionHandlerTest {

	@Test()
	public void globalExceptionManagerTest(){
		System.out.println(
			new GlobalExceptionManager().AuthExceptionHandler(
				new AuthException(ErrorCode.NO_AUTHORITY, "권한이 없는 유저입니다.")
			)
		);
	}
	@Test()
	public void globalExceptionManagerTestWithFieldAndGivenData(){
		System.out.println(
			new GlobalExceptionManager().AuthExceptionHandler(
				new AuthException(ErrorCode.NO_AUTHORITY, "권한이 없는 유저입니다.","username","user1")
			)
		);
	}

}
