package com.wy.service;

public interface AccountInfoService {

	// 账户扣款
	void updateAccountBalance(String accountNo, Double amount);
}