package com.hien.billing.api.infra.jdbc;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DBConfig {

	@Value("${db.main.host}")
	String dbHost;

	@Value("${db.main.username}")
	String dbUsername;

	@Value("${db.main.password}")
	String dbPassword;

	@Value("${db.main.name}")
	String dbName;

	@Value("${db.main.pool:10}")
	int dbPoolMaxSize;

	@Bean
	public HikariDataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(
				"jdbc:mysql://" + dbHost + ":3306/" + dbName + "?" +
						"createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&autoReconnect=true");

		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPassword);
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setMaximumPoolSize(dbPoolMaxSize);

		Properties dataSourceProps = new Properties();
		dataSourceProps.setProperty("cachePrepStmts", "true");
		dataSourceProps.setProperty("prepStmtCacheSize", "250");
		dataSourceProps.setProperty("prepStmtCacheSqlLimit", "2048");
		dataSourceProps.setProperty("useServerPrepStmts", "true");

		dataSource.setDataSourceProperties(dataSourceProps);

		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource());
	}
}