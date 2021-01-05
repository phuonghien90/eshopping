package com.hien.base.log;

import org.apache.log4j.Logger;

import com.hien.base.JsonSerializer;

public class LogHelpers {
	public static final Logger logger = Logger.getLogger(LogHelpers.class);

	public static void error(Throwable tr) {
		LogObj log = new LogObj("exception");
		log.error(tr);
		logger.error(JsonSerializer.prettyPrintObject2Json(log));
	}

	public static void error(String message, Throwable tr) {
		LogObj log = new LogObj("exception");
		log.debug(message);
		log.error(tr);
		logger.error(JsonSerializer.prettyPrintObject2Json(log));
	}

	public static void errorIfFailure(LogObj log) {
		if ("failure".equals(log.status)) {
			logger.error(JsonSerializer.prettyPrintObject2Json(log));
		} else {
			logger.info(JsonSerializer.prettyPrintObject2Json(log));
		}
	}

	public static void info(LogObj log) {
		logger.info(JsonSerializer.prettyPrintObject2Json(log));
	}
}
