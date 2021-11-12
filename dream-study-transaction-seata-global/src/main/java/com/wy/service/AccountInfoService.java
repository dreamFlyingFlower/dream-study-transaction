package com.wy.service;

public interface AccountInfoService {

	// 张三扣减金额
	void updateAccountBalance(String accountNo, Double amount);
}