package com.edu.binghamton.cs571;

public class ArithmeticParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = null;

	public ArithmeticParserException() {
		super();
	}

	public ArithmeticParserException(String message) {
		super(message);
		this.message = message;

	}

	@Override
	public String getMessage() {
		return message;
	}
}
