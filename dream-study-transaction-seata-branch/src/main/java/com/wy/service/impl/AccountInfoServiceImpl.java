package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wy.mapper.AccountInfoMapper;
import com.wy.service.AccountInfoService;

import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

	@Autowired
	AccountInfoMapper accountInfoDao;

	@Transactional
	@Override
	public void updateAccountBalance(String accountNo, Double amount) {
		log.info("bank2 service begin,XID：{}", RootContext.getXID());
		// 李四增加金额
		accountInfoDao.updateAccountBalance(accountNo, amount);
		if (amount == 3) {
			// 人为制造异常
			throw new RuntimeException("bank2 make exception..");
		}
	}
}