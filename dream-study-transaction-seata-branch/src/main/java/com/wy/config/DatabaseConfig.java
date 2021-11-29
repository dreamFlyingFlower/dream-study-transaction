package com.wy.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;

import io.seata.rm.datasource.DataSourceProxy;

@Configuration
public class DatabaseConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.ds0")
	public DruidDataSource ds0() {
		return new DruidDataSource();
	}

	@Primary
	@Bean
	public DataSource dataSource(DruidDataSource ds0) {
		return new DataSourceProxy(ds0);
	}
}