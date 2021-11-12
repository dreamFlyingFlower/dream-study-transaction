package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.service.AccountInfoService;

@RestController
public class Bank1Crl {

	@Autowired
	AccountInfoService accountInfoService;

	// 张三转账
	@GetMapping("/transfer")
	public String transfer(Double amount) {
		accountInfoService.updateAccountBalance("1", amount);
		return "bank1" + amount;
	}
}