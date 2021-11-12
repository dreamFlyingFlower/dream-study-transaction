package com.wy.service;

import com.wy.model.AccountChangeEvent;

public interface AccountInfoService {

	// 向mq发送转账消息
	void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent);

	// 更新账户,扣减金额
	void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent);
}