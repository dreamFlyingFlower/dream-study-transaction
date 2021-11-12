package com.wy.service;

import com.wy.model.AccountChangeEvent;

public interface AccountInfoService {

	// 更新账户,增加金额
	void addAccountInfoBalance(AccountChangeEvent accountChangeEvent);
}