package com.wy.service.impl;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.wy.service.UserService;

/**
 * 单体事务以及事务失效
 * 
 * @author 飞花梦影
 * @date 2021-10-30 16:22:41
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Transactional(rollbackFor = Exception.class)
	public void test01() {
		// 若此时发生了异常,且异常被抛出,事务生效
		test02();
		try {
			// 若此时发生了异常,但是异常被捕获,事务失效
			test02();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 若此时发生异常,由于是由this直接调用,不走AOP的动态代理,无法捕获异常,事务失效
		test02();
		// 使用AopContext调用test02,此时若发生异常,可被AOP捕获,事务生效
		UserServiceImpl userServiceImpl = (UserServiceImpl) AopContext.currentProxy();
		userServiceImpl.test02();
		// 使用ApplicationContext获得代理对象,事务生效
		UserServiceImpl userServiceImpl2 = applicationContext.getBean(UserServiceImpl.class);
		userServiceImpl2.test02();
	}

	@Override
	public String test02() {
		return null;
	}

	/**
	 * 当需要进行事务的操作中耗时较长时,开启事务将会一直占用数据库连接,在高并发时将会影响程序性能.
	 * 可使用TransactionTemplate对部分需要进行数据库操作的代码块进行事务操作,而不是整个方法全部进行事务
	 * 
	 * 当该方法被其他含有事务的方法调用时,可以设置本方法的Transactional不使用事务
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void test03() {
		// 假设此处的RPC调用可能需要20多秒完成,高并发可能会造成数据库宕机
		transactionTemplate.execute((param) -> {
			// 需要进行事务的操作
			return null;
		});
	}
}