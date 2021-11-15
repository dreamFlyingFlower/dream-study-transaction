package com.wy.service;

import com.wy.model.AccountChangeEvent;
import com.wy.model.AccountPay;

public interface AccountInfoService {

	// 更新账户金额
	void updateAccountBalance(AccountChangeEvent accountChange);

	// 查询充值结果(远程调用)
	AccountPay queryPayResult(String tx_no);
}