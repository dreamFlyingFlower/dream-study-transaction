package com.wy.service;

import com.wy.model.AccountPay;

public interface AccountPayService {

	// 充值
	AccountPay insertAccountPay(AccountPay accountPay);

	// 查询充值结果
	AccountPay getAccountPay(String txNo);
}