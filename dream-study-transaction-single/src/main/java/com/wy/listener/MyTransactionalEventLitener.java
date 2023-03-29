package com.wy.listener;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.wy.event.MyTransactionalEvent;

/**
 * 事务监听器,监听事务变化,用来观察事务进程
 *
 * @author 飞花梦影
 * @date 2023-03-29 23:32:15
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class MyTransactionalEventLitener {

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void commit(MyTransactionalEvent event) {
		// 监听事务的对象,该对象中为发布监听时传入的参数,见MyTransactionalPublisher
		Map<Object, Object> source = event.getSource();
		System.out.println(source);
	}
}