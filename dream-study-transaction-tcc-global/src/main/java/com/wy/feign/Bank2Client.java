package com.wy.feign;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "dream-study-transaction-tcc-branch", fallback = Bank2ClientFallback.class)
public interface Bank2Client {

	// 远程调用李四的微服务
	@GetMapping("/tcc-branch/transfer")
	@Hmily
	Boolean transfer(@RequestParam("amount") Double amount);
}