package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 分布式事务:最大努力通知,需要使用中间件.本微服务为调用方
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@EnableFeignClients(basePackages = { "com.wy.feign" })
@EnableHystrix
@SpringBootApplication
public class NotifyGlobalApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifyGlobalApplication.class, args);
	}
}