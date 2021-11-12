package com.wy.service.impl;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.wy.mapper.AccountInfoMapper;
import com.wy.model.AccountChangeEvent;
import com.wy.service.AccountInfoService;

/**
 * 使用RocketMQ完成消息一致性
 * 
 * @author 飞花梦影
 * @date 2021-11-12 11:02:13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Service
public class AccountInfoServiceImpl implements AccountInfoService {

	@Autowired
	AccountInfoMapper accountInfoMapper;

	@Autowired
	RocketMQTemplate rocketMQTemplate;

	// 向mq发送转账消息
	@Override
	public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {

		// 将accountChangeEvent转成json
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("accountChange", accountChangeEvent);
		String jsonString = jsonObject.toJSONString();
		// 生成message类型
		Message<String> message = MessageBuilder.withPayload(jsonString).build();
		// 发送一条事务消息
		/**
		 * String txProducerGroup 生产组 String destination topic,Message<?> message, 消息内容
		 * Object arg 参数
		 */
		rocketMQTemplate.sendMessageInTransaction("producer_group_txmsg_bank1", "topic_txmsg", message, null);

	}

	// 更新账户,扣减金额
	@Override
	@Transactional
	public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
		// 幂等判断
		if (accountInfoMapper.isExistTx(accountChangeEvent.getTxNo()) > 0) {
			return;
		}
		// 扣减金额
		accountInfoMapper.updateAccountBalance(accountChangeEvent.getAccountNo(), accountChangeEvent.getAmount() * -1);
		// 添加事务日志
		accountInfoMapper.addTx(accountChangeEvent.getTxNo());
		if (accountChangeEvent.getAmount() == 3) {
			throw new RuntimeException("人为制造异常");
		}
	}
}