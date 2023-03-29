package com.wy.publisher;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.wy.event.MyTransactionalEvent;

/**
 * 发布事务监听
 *
 * @author 飞花梦影
 * @date 2023-03-29 23:42:16
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class MyTransactionalPublisher {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public void test() {
		// 发布监听事件时携带的参数,可被监听器获取
		Map<Object, Object> params = new HashMap<>();
		applicationEventPublisher.publishEvent(new MyTransactionalEvent(params));
	}
}