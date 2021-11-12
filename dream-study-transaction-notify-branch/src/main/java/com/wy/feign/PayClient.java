package com.wy.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.wy.model.AccountPay;

/**
 * 远程调用pay充值系统
 * 
 * @author 飞花梦影
 * @date 2021-11-12 14:21:55
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@FeignClient(value = "dtx-notifymsg-demo-pay", fallback = PayFallback.class)
public interface PayClient {

	// 远程调用充值系统的接口查询充值结果
	@GetMapping(value = "/pay/payresult/{txNo}")
	AccountPay payresult(@PathVariable("txNo") String txNo);
}