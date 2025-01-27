package com.amcamp.exception;


import com.amcamp.global.common.exception.auth.AuthErrorCode;
import com.amcamp.global.common.exception.GlobalExceptionManager;
import com.amcamp.global.common.exception.auth.AuthException;
import org.junit.jupiter.api.Test;

public class AuthExceptionHandlerTest {

	@Test()
	public void globalExceptionManagerTest(){
		System.out.println(
			new GlobalExceptionManager().AuthExceptionHandler(
				new AuthException(AuthErrorCode.NO_AUTHORITY)
			)
		);
	}
	@Test()
	public void globalExceptionManagerTestWithFieldAndGiven(){
		System.out.println(
			new GlobalExceptionManager().AuthExceptionHandler(
				new AuthException(AuthErrorCode.NO_AUTHORIZED_USER,"username","user1")
			)
		);
	}

}
