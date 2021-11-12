package com.wy.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "dream-study-transaction-seata-branch", fallback = Bank2ClientFallback.class)
public interface Bank2Client {

	// 远程调用李四的微服务
	@GetMapping("/bank2/transfer")
	String transfer(@RequestParam("amount") Double amount);
}