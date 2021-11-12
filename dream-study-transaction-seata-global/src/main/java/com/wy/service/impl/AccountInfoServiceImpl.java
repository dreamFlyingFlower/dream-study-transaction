package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wy.feign.Bank2Client;
import com.wy.mapper.AccountInfoMapper;
import com.wy.service.AccountInfoService;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link GlobalTransactionContext}:开启全局事务
 * 
 * @author 飞花梦影
 * @date 2021-11-12 16:34:58
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

	@Autowired
	AccountInfoMapper accountInfoDao;

	@Autowired
	Bank2Client bank2Client;

	@GlobalTransactional
	@Transactional
	@Override
	public void updateAccountBalance(String accountNo, Double amount) {
		log.info("bank1 service begin,XID：{}", RootContext.getXID());
		// 扣减张三的金额
		accountInfoDao.updateAccountBalance(accountNo, amount * -1);
		// 调用李四微服务,转账
		String transfer = bank2Client.transfer(amount);
		if ("fallback".equals(transfer)) {
			// 调用李四微服务异常
			throw new RuntimeException("调用李四微服务异常");
		}
		if (amount == 2) {
			// 人为制造异常
			throw new RuntimeException("bank1 make exception..");
		}
	}
}