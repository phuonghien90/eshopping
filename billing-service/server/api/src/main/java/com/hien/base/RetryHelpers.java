package com.hien.base;

import java.util.Collections;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

public class RetryHelpers {

	public static <T, E extends Throwable> T retry(int times, RetryCallback<T, E> callback) throws E {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(times, Collections.singletonMap(
				Throwable.class, Boolean.valueOf(true))));
		return retryTemplate.execute(callback);
	}

}
