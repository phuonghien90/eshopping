package com.hien.eshopping.api;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.DataSourceHealthIndicator;
import org.springframework.boot.actuate.health.DiskSpaceHealthIndicator;
import org.springframework.boot.actuate.health.DiskSpaceHealthIndicatorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthCheck {

	@Autowired
	DataSource dataSource;

	@Bean
	public DataSourceHealthIndicator dataSourceHealthIndicator() {
		return new DataSourceHealthIndicator(dataSource);
	}

	@Bean
	public DiskSpaceHealthIndicator diskSpaceHealthIndicator() {
		return new DiskSpaceHealthIndicator(new DiskSpaceHealthIndicatorProperties());
	}
}
