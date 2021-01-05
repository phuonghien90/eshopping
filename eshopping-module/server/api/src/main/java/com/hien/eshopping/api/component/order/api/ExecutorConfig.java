package com.hien.eshopping.api.component.order.api;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {

	@Bean(name = "getVoucherExecutor")
	public ThreadPoolTaskExecutor getVoucherExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(80);
		executor.setMaxPoolSize(500);
		executor.setQueueCapacity(100);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setThreadNamePrefix("executors-get-voucher");
		return executor;
	}
}
