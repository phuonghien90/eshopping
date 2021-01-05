package com.hien.base.exception;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hien.base.CustomImmutableMapBuilder;

@ControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Object validationExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
		return CustomImmutableMapBuilder.of(
				"code", ex.getClass().getSimpleName());
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Object servletExceptionHandler(HttpServletRequest req, ServletException ex) {
		return CustomImmutableMapBuilder.of(
				"code", ex.getClass().getSimpleName());
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Object springCannotReadRequestBody(HttpServletRequest req, HttpMessageConversionException ex) {
		return CustomImmutableMapBuilder.of(
				"code", ex.getClass().getSimpleName());
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Object xExceptionHandler(HttpServletRequest req, CustomException ex) {
		return CustomImmutableMapBuilder.of(
				"code", ex.code);
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public Object resourceNotFoundExceptionHandler(HttpServletRequest req, ResourceNotFoundException ex) {
		return CustomImmutableMapBuilder.of(
				"code", ex.code);
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Object internalServerErrorHandler(HttpServletRequest req, Throwable ex) {
		return CustomImmutableMapBuilder.of(
				"code", "internalServerError");
	}
}
