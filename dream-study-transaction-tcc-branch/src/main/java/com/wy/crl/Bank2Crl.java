package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wy.service.AccountInfoService;

@RestController
public class Bank2Crl {

	@Autowired
	AccountInfoService accountInfoService;

	@RequestMapping("/transfer")
	public Boolean transfer(@RequestParam("amount") Double amount) {
		this.accountInfoService.updateAccountBalance("2", amount);
		return true;
	}
}