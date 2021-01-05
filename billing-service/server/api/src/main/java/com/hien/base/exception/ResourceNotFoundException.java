package com.hien.base.exception;

public class ResourceNotFoundException extends CustomException {

	private static final long serialVersionUID = -5855592210950289661L;

	public ResourceNotFoundException(String resourceName) {
		super(String.format("%sNotFound", resourceName.toLowerCase()));
	}
}
