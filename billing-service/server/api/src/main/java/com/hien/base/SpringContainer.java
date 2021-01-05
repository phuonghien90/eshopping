package com.hien.base;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("springApplicationContextHolder")
public class SpringContainer implements ApplicationContextAware {
	private static ApplicationContext applicationContext = null;

	public static ApplicationContext ctx() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContainer.applicationContext = applicationContext;
	}

	public static boolean runningOnEnviroments(String... envs) {
		List<String> envList = Arrays.asList(envs);

		return Arrays.stream(ctx().getEnvironment().getActiveProfiles())
				.anyMatch(env -> envList.contains(env));
	}
}
