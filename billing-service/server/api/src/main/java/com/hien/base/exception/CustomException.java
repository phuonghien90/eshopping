package com.hien.base.exception;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 3884882248703972427L;

	public final String code;

	public CustomException() {
		this("internalServerError");
	}

	public CustomException(String code) {
		this(code, code);
	}

	public CustomException(String message, String code) {
		super(determineMessage(message, code));
		code = determineCode(message, code);
		this.code = code;
	}

	public CustomException(String message, String code, Throwable cause) {
		super(determineMessage(message, code), cause);
		code = determineCode(message, code);
		this.code = code;
	}

	/**
	 * Message is the one that contains space, otherwise follows param name
	 */
	private static String determineCode(String message, String code) {
		if (message.contains(" ") && code.contains(" ")) {
			return code;
		} else if (message.contains(" ") && !code.contains(" ")) {
			return code;
		} else if (!message.contains(" ") && code.contains(" ")) {
			return message;
		} else {
			return code;
		}
	}

	/**
	 * Message is the one that contains space, otherwise follows param name
	 */
	private static String determineMessage(String message, String code) {
		if (message.contains(" ") && code.contains(" ")) {
			return message;
		} else if (message.contains(" ") && !code.contains(" ")) {
			return message;
		} else if (!message.contains(" ") && code.contains(" ")) {
			return code;
		} else {
			return message;
		}
	}
}
