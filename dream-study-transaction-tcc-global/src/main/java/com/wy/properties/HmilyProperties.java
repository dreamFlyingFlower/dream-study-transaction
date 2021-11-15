package com.wy.properties;

import org.dromara.hmily.common.config.HmilyConfig;
import org.dromara.hmily.common.config.HmilyDbConfig;
import org.dromara.hmily.common.enums.RepositorySupportEnum;
import org.dromara.hmily.common.enums.SerializeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * Hmily配置,参考{@link HmilyConfig}
 * 
 * @author 飞花梦影
 * @date 2021-11-13 15:57:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class HmilyProperties {

	/**
	 * 仓库后缀,前缀是hmily,后缀默认是服务名,不可重复
	 */
	private String repositorySuffix = "tcc-global";

	/**
	 * 指定事务日志的序列化方式. {@linkplain SerializeEnum}
	 */
	private String serializer = "kryo";

	/**
	 * 调度线程数最大线程数量,默认CPU * 2
	 */
	private int scheduledThreadMax = 10;

	/**
	 * 调度任务执行延迟间隔时间,单位秒
	 */
	private int scheduledDelay = 60;

	/**
	 * 调度任务启动延迟时间,单位秒
	 */
	private int scheduledInitDelay = 120;

	/**
	 * 最大重试次数
	 */
	private int retryMax = 10;

	/**
	 * 事务日志恢复迟延时间,单位秒
	 */
	private int recoverDelayTime = 60;

	/**
	 * 事务存储仓库类型. {@linkplain RepositorySupportEnum}
	 */
	private String repositorySupport = "db";

	/**
	 * disruptor的bufferSize大小,单位字节
	 */
	private int bufferSize = 4096 * 2 * 2;

	/**
	 * disruptor消费者线程数量,默认CPU * 2
	 */
	private int consumerThreads = Runtime.getRuntime().availableProcessors() << 1;

	/**
	 * 异步执行confirm,cancel线程数
	 */
	private int asyncThreads = Runtime.getRuntime().availableProcessors() << 1;

	/**
	 * 全局事务的发起方为true,分支事务为false
	 */
	private Boolean started = true;

	/**
	 * 数据库配置
	 */
	private HmilyDbConfig hmilyDbConfig;
}