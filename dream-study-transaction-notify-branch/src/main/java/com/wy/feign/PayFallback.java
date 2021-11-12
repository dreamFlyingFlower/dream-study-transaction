package com.wy.feign;

import org.springframework.stereotype.Component;

import com.wy.model.AccountPay;

@Component
public class PayFallback implements PayClient {

	@Override
	public AccountPay payresult(String txNo) {
		AccountPay accountPay = new AccountPay();
		accountPay.setResult("fail");
		return accountPay;
	}
}