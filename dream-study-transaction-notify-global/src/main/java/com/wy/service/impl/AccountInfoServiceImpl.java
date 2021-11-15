package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wy.feign.PayClient;
import com.wy.mapper.AccountInfoMapper;
import com.wy.model.AccountChangeEvent;
import com.wy.model.AccountPay;
import com.wy.service.AccountInfoService;

@Service
public class AccountInfoServiceImpl implements AccountInfoService {

	@Autowired
	AccountInfoMapper accountInfoDao;

	@Autowired
	PayClient payClient;

	// 更新账户金额
	@Override
	@Transactional
	public void updateAccountBalance(AccountChangeEvent accountChange) {
		// 幂等校验
		if (accountInfoDao.isExistTx(accountChange.getTxNo()) > 0) {
			return;
		}
		accountInfoDao.updateAccountBalance(accountChange.getAccountNo(), accountChange.getAmount());
		// 插入事务记录,用于幂等控制
		accountInfoDao.addTx(accountChange.getTxNo());
	}

	// 远程调用查询充值结果
	@Override
	public AccountPay queryPayResult(String tx_no) {
		// 远程调用
		AccountPay payresult = payClient.payresult(tx_no);
		if ("success".equals(payresult.getResult())) {
			// 更新账户金额
			AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
			accountChangeEvent.setAccountNo(payresult.getAccountNo());
			accountChangeEvent.setAmount(payresult.getPayAmount());
			// 充值事务号
			accountChangeEvent.setTxNo(payresult.getId());
			updateAccountBalance(accountChangeEvent);
		}
		return payresult;
	}
}