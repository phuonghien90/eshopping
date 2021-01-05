package com.hien.billing.api;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.hien.base.log.LogHelpers;
import com.hien.base.log.LogObj;

@SpringBootApplication
@ComponentScan({ "com.hien" })
@EnableAutoConfiguration(exclude = {
		ErrorMvcAutoConfiguration.class
})
@Configuration
@EnableScheduling
@EnableDiscoveryClient
public class Application {

	public static void main(String[] args) {
		LogObj log = new LogObj("startServer");
		try {
			SpringApplication.run(Application.class);
		} catch (Throwable tr) {
			log.error(tr);
		} finally {
			LogHelpers.errorIfFailure(log);
		}
	}

	@PostConstruct
	public void init() {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			LogHelpers.error("unhandledException", e);
		});
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedHeaders("*")
						.allowedMethods("*");
			}
		};
	}
}