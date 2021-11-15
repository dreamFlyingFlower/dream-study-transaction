package com.wy.service.impl;

import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wy.mapper.AccountInfoMapper;
import com.wy.service.AccountInfoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

	@Autowired
	AccountInfoMapper accountInfoMapper;

	/**
	 * 当前微服务方法为被调用方,不能主动发起跨服务事务,否则会抛异常,只能被其他微服务调用
	 */
	@Override
	@Hmily(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
	public void updateAccountBalance(String accountNo, Double amount) {
		// 获取全局事务id
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("bank2 try begin 开始执行...xid:{}", transId);
	}

	/**
	 * confirm方法 confirm幂等校验 正式增加金额
	 * 
	 * @param accountNo
	 * @param amount
	 */
	@Transactional
	public void confirmMethod(String accountNo, Double amount) {
		// 获取全局事务id
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("bank2 confirm begin 开始执行...xid:{}", transId);
		if (accountInfoMapper.isExistConfirm(transId) > 0) {
			log.info("bank2 confirm 已经执行，无需重复执行...xid:{}", transId);
			return;
		}
		// 增加金额
		accountInfoMapper.addAccountBalance(accountNo, amount);
		// 增加一条confirm日志,用于幂等
		accountInfoMapper.addConfirm(transId);
		log.info("bank2 confirm end 结束执行...xid:{}", transId);
	}

	/**
	 * 取消事务
	 * 
	 * @param accountNo
	 * @param amount
	 */
	public void cancelMethod(String accountNo, Double amount) {
		// 获取全局事务id
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("bank2 cancel begin 开始执行...xid:{}", transId);
	}
}