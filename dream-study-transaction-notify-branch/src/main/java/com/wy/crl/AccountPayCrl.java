package com.wy.crl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.wy.model.AccountPay;
import com.wy.service.AccountPayService;

@RestController
public class AccountPayCrl {

	@Autowired
	AccountPayService accountPayService;

	// 充值
	@GetMapping(value = "/paydo")
	public AccountPay pay(AccountPay accountPay) {
		// 生成事务编号
		String txNo = UUID.randomUUID().toString();
		accountPay.setId(txNo);
		return accountPayService.insertAccountPay(accountPay);
	}

	// 查询充值结果
	@GetMapping(value = "/payresult/{txNo}")
	public AccountPay payresult(@PathVariable("txNo") String txNo) {
		return accountPayService.getAccountPay(txNo);
	}
}