package com.wy.message;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.wy.mapper.AccountInfoMapper;
import com.wy.model.AccountChangeEvent;
import com.wy.service.AccountInfoService;

/**
 * 使用RocketMQ完成消息一致性,实现{@link RocketMQLocalTransactionListener}
 * 
 * @author 飞花梦影
 * @date 2021-11-12 11:08:55
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@SuppressWarnings("rawtypes")
@Component
@RocketMQTransactionListener(txProducerGroup = "producer_group_txmsg_bank1")
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {

	@Autowired
	AccountInfoService accountInfoService;

	@Autowired
	AccountInfoMapper accountInfoMapper;

	/**
	 * 事务消息发送后的回调方法,当消息发送给mq成功,此方法被回调
	 * 
	 * 发送prepare消息成功后此方法被回调,该方法用于执行本地事务
	 * 
	 * @param message 回传的消息,利用transactionId即可获取到该消息的唯一Id
	 * @param object 调用send方法时传递的参数,当send时候若有额外的参数可以传递到send方法中,这里能获取到
	 * @return 返回事务状态.COMMIT->提交;ROLLBACK->回滚;UNKNOW->回调
	 */
	@Override
	@Transactional
	public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object object) {
		try {
			// 解析message,转成AccountChangeEvent
			String messageString = new String((byte[]) message.getPayload());
			JSONObject jsonObject = JSONObject.parseObject(messageString);
			String accountChangeString = jsonObject.getString("accountChange");
			// 将accountChange(json)转成AccountChangeEvent
			AccountChangeEvent accountChangeEvent =
					JSONObject.parseObject(accountChangeString, AccountChangeEvent.class);
			// 执行本地事务,扣减金额
			accountInfoService.doUpdateAccountBalance(accountChangeEvent);
			// 当返回RocketMQLocalTransactionState.COMMIT,自动向mq发送commit消息,mq将消息的状态改为可消费
			return RocketMQLocalTransactionState.COMMIT;
		} catch (Exception e) {
			e.printStackTrace();
			return RocketMQLocalTransactionState.ROLLBACK;
		}
	}

	/**
	 * 事务状态回查,查询是否扣减金额
	 * 
	 * @param msg 通过获取transactionId来判断这条消息的本地事务执行状态
	 * @return 返回事务状态.COMMIT->提交;ROLLBACK->回滚;UNKNOW->回调
	 */
	@Override
	public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
		// 解析message,转成AccountChangeEvent
		String messageString = new String((byte[]) message.getPayload());
		JSONObject jsonObject = JSONObject.parseObject(messageString);
		String accountChangeString = jsonObject.getString("accountChange");
		// 将accountChange(json)转成AccountChangeEvent
		AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChangeString, AccountChangeEvent.class);
		// 事务id
		String txNo = accountChangeEvent.getTxNo();
		int existTx = accountInfoMapper.isExistTx(txNo);
		if (existTx > 0) {
			return RocketMQLocalTransactionState.COMMIT;
		} else {
			return RocketMQLocalTransactionState.UNKNOWN;
		}
	}
}