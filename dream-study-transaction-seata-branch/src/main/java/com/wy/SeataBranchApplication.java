package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * 分布式事务
 * 
 * SpringCloud Alibaba Seata:需要下载对应的程序,并且配置一系列配置,如数据库配置,注册中心等
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@EnableHystrix
@SpringBootApplication
public class SeataBranchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeataBranchApplication.class, args);
	}
}