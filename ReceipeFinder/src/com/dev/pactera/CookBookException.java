package com.dev.pactera;

public class CookBookException extends Exception {
	
	String message;
	Throwable throwable;
	
	CookBookException(String message,Throwable throwable)
	{
		this.message = message;
		this.throwable = throwable;
	}

}
