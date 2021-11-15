package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.wy.model.AccountPay;
import com.wy.service.AccountInfoService;

@RestController
public class AccountInfoCrl {

	@Autowired
	private AccountInfoService accountInfoService;

	// 主动查询充值结果
	@GetMapping(value = "/payresult/{txNo}")
	public AccountPay result(@PathVariable("txNo") String txNo) {
		AccountPay accountPay = accountInfoService.queryPayResult(txNo);
		return accountPay;
	}
}