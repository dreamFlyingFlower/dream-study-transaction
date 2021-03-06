package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 分布式事务Seata,有XA,TCC模式,但是TCC没有和SpringCloud整合,此处只使用XA模式进行演示
 * 
 * SpringCloud Alibaba Seata:需要下载对应的程序,并且配置一系列配置,如数据库配置,注册中心等
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableEurekaClient
@EnableHystrix
@EnableFeignClients(basePackages = { "com.wy.feign" })
@SpringBootApplication
public class SeataGlobalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeataGlobalApplication.class, args);
	}
}