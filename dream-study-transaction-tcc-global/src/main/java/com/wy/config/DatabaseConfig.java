package com.wy.config;

import org.dromara.hmily.core.bootstrap.HmilyTransactionBootstrap;
import org.dromara.hmily.core.service.HmilyInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.alibaba.druid.pool.DruidDataSource;
import com.wy.properties.GlobalProperties;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DatabaseConfig {

	@Autowired
	private GlobalProperties config;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.ds0")
	public DruidDataSource ds0() {
		return new DruidDataSource();
	}

	@Bean
	public HmilyTransactionBootstrap hmilyTransactionBootstrap(HmilyInitService hmilyInitService) {
		HmilyTransactionBootstrap hmilyTransactionBootstrap = new HmilyTransactionBootstrap(hmilyInitService);
		hmilyTransactionBootstrap.setRepositorySuffix(config.getHmily().getRepositorySuffix());
		hmilyTransactionBootstrap.setSerializer(config.getHmily().getSerializer());
		hmilyTransactionBootstrap.setRecoverDelayTime(config.getHmily().getRecoverDelayTime());
		hmilyTransactionBootstrap.setRetryMax(config.getHmily().getRetryMax());
		hmilyTransactionBootstrap.setScheduledDelay(config.getHmily().getScheduledDelay());
		hmilyTransactionBootstrap.setScheduledThreadMax(config.getHmily().getScheduledThreadMax());
		hmilyTransactionBootstrap.setRepositorySupport(config.getHmily().getRepositorySupport());
		hmilyTransactionBootstrap.setStarted(config.getHmily().getStarted());
		hmilyTransactionBootstrap.setHmilyDbConfig(config.getHmily().getHmilyDbConfig());
		return hmilyTransactionBootstrap;
	}

	/*
	 * @Bean
	 * 
	 * @ConfigurationProperties(prefix = "org.dromara.hmily") public HmilyConfig
	 * hmilyConfig(){ return new HmilyConfig(); }
	 * 
	 * @Bean public HmilyTransactionBootstrap
	 * hmilyTransactionBootstrap(HmilyInitService hmilyInitService, HmilyConfig
	 * hmilyConfig){ HmilyTransactionBootstrap hmilyTransactionBootstrap = new
	 * HmilyTransactionBootstrap(hmilyInitService); return
	 * hmilyTransactionBootstrap; }
	 */
}