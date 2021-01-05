package com.hien.base.exception;

import org.apache.commons.lang3.StringUtils;

public class InvalidException extends CustomException {

	private static final long serialVersionUID = 3871187769288714112L;

	public InvalidException(String resourceName) {
		super(String.format("invalid%s", StringUtils.capitalize(resourceName.toLowerCase())));
	}
}
