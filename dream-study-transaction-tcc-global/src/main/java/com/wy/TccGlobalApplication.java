package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;

/**
 * 分布式事务TCC模型,可以使用tcc-transaction,hmily等,tcc-transaction需要搭建额外的服务,此处使用hmily进行演示
 * 
 * Hmily使用
 * 
 * <pre>
 * 1.需要手动创建hmily数据库,用来存储hmily的事务数据
 * 		CREATE DATABASE `hmily` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci';
 * 2.创建bank1,bank2数据库,见父工程中的SQL
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@EnableHystrix
@EnableAspectJAutoProxy
@EnableFeignClients(basePackages = { "com.wy.feign" })
@SpringBootApplication(scanBasePackages = { "com.wy", "org.dromara.hmily" },exclude = SeataAutoConfiguration.class)
public class TccGlobalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TccGlobalApplication.class, args);
	}
}