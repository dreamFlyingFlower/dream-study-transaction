package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * 分布式事务:最大努力通知,需要使用中间件,内部系统分支事务或第三方系统事务
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@EnableHystrix
@SpringBootApplication
public class NotifyBranchApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifyBranchApplication.class, args);
	}
}