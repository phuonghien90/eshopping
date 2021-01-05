package com.hien.eshopping.api.infra.jdbc;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.sources.JDBCConfigurationSource;

@Configuration
public class ArchaiusConfig {

	@Autowired
	DataSource dataSource;

	@Bean
	public JDBCConfigurationSource configurationSource() {
		return new JDBCConfigurationSource(dataSource, "SELECT * FROM configuration", "key", "value");
	}

	@Bean
	public FixedDelayPollingScheduler configurationPollingScheduler() {
		return new FixedDelayPollingScheduler(10000, 60000, false);
	}

	@Bean
	public DynamicConfiguration dynamicConfiguration() {
		return new DynamicConfiguration(configurationSource(), configurationPollingScheduler());
	}

	@PostConstruct
	public void addDynamicConfig() {
		ConfigurationManager.loadPropertiesFromConfiguration(dynamicConfiguration());
	}
}
