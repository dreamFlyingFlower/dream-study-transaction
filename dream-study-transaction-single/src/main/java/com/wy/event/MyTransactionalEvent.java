package com.wy.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

import com.wy.publisher.MyTransactionalPublisher;

/**
 * 接收自定义监听事务触发的事件
 *
 * @author 飞花梦影
 * @date 2023-03-29 23:33:19
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class MyTransactionalEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2688089074259525312L;

	/**
	 * 发布监听事件时的传入的参数,见{@link MyTransactionalPublisher}
	 */
	private Map<Object, Object> source;

	public MyTransactionalEvent(Map<Object, Object> source) {
		super(source);
		this.source = source;
	}

	@Override
	public Map<Object, Object> getSource() {
		return this.source;
	}
}