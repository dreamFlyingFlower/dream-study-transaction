package com.wy;

import org.aopalliance.aop.Advice;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * SpringBoot事务
 * 
 * 事务的特性:原子性,隔离性,一致性,持久性
 * 
 * 为保证事务的一致性而产生的各种问题:
 * 
 * <pre>
 * 脏读:一个事务读到另一个事务未提交的数据
 * 不可重复读:一个事务读到另一个事务已经提交的update数据,导致一个事务中多次查询结果不一致
 * 虚读,幻读:一个事务读到另一个事务已经提交的insert或delete数据,导致一个事务中多次查询结果数量不一致
 * </pre>
 * 
 * 为解决事务的一致性问题而产生的隔离机制:
 * 
 * <pre>
 * Read uncommited:未提交读,任何问题都解决不了,但是效率最高
 * Read commited:已提交读,解决赃读,oracle使用这种默认方式
 * Repeatable read:重复读,解决赃读和不可重复读,mysql默认使用的这种方式,且在新版的mysql中已经能解决所有问题
 * Serialzable:解决所有问题,但是不可并发, 效率最低
 * {@link TransactionDefinition#ISOLATION_DEFAULT}:使用数据库自己默认的隔离机制
 * {@link TransactionDefinition#ISOLATION_READ_UNCOMMITTED}:读未提交
 * {@link TransactionDefinition#ISOLATION_READ_COMMITTED}:已读提交
 * {@link TransactionDefinition#ISOLATION_REPEATABLE_READ}:可重复读
 * {@link TransactionDefinition#ISOLATION_SERIALIZABLE}:严格的一个一个读
 * </pre>
 * 
 * 事务的传播机制,解决事务的嵌套问题.例如A()中调用了B(),是否有多个事务可以使用,或者只使用一个事务,或不使用事务
 * 
 * <pre>
 * {@link TransactionDefinition#PROPAGATION_REQUIRED}:默认,若A有事务,则使用A的事务;若没有则新建事务
 * {@link TransactionDefinition#PROPAGATION_SUPPORTS}:若A有事务,则使用A的事务;若A没有事务,就不使用事务
 * {@link TransactionDefinition#PROPAGATION_MANDATORY}:若A有事务,则使用A的事务;若A没有事务,就抛出异常
 * {@link TransactionDefinition#PROPAGATION_REQUIRES_NEW}:若A有事务,将A事务挂起,新建一个事务,且只作用于B
 * {@link TransactionDefinition#PROPAGATION_NOT_SUPPORTED}:非事务方式执行操作,不管A,B有事务,都将事务挂起执行
 * {@link TransactionDefinition#PROPAGATION_NEVER}:非事务方式执行,若A,B中任何一个有事务,直接抛异常
 * {@link TransactionDefinition#PROPAGATION_NESTED}:若存在事务,则在嵌套事务内执行;若没有事务,则与REQUIRED类似
 * </pre>
 * 
 * Spring事务动态代理原理--注册相关bean:
 * 
 * <pre>
 * 1.{@link AbstractApplicationContext#refresh()}:启动容器时刷新配置
 * 2.{@link AbstractApplicationContext#invokeBeanFactoryPostProcessors}:进行后置处理
 * 3.{@link ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry()}:注册beanDefinition
 * 4.{@link ConfigurationClassPostProcessor#processConfigBeanDefinitions()}:注册beanDefinition
 * 5.{@link #ConfigurationClassParser#parse()}:解析扫描启动类以及自动配置类
 * 6.{@link #ConfigurationClassParser#doProcessConfigurationClass()}:解析启动类以及自动配置类,加载@Bean,@Import等相关注解
 * 7.{@link #ConfigurationClassParser#processImports()}:解析自动配置类以及{@link ImportSelector},{@link DeferredImportSelector}
 * 8.{@link #ConfigurationClassParser$DeferredImportSelectorHandler#handle()}:解析AutoConfigurationImportSelector,
 * 		该类由{@link EnableAutoConfiguration}引入,加载所有自动配置类
 * 9.{@link TransactionAutoConfiguration}:被8处理,自动配置
 * 10.{@link EnableTransactionManagement}:上一步引入,设置事务使用代理或切面,默认是PROXY代理
 * 11.{@link AdviceModeImportSelector#selectImports}:由7处理,根据事务引入类型引入注册指定类
 * 12.{@link TransactionManagementConfigurationSelector#selectImports}:非ImportSelector中接口,间接由AdviceModeImportSelector导入
 * 13.{@link AutoProxyRegistrar#registerBeanDefinitions}:由7进行解析注入.事务处理增强器,优先级比AOP的AnnotationAwareAspectJAutoProxyCreator低.
 * 		根据最近的含有mode和proxyTargetClass注解注入这2个属性,此处最近的注解为EnableTransactionManagement.
 * 		最终将会给容器中注册一个 InfrastructureAdvisorAutoProxyCreator 实例,利用后置处理器机制在对象创建以后,包装对象,
 * 		返回一个代理对象(增强器),代理对象执行方法,利用拦截器链进行调用
 * 14.{@link ProxyTransactionManagementConfiguration}:处理事务配置,由{@link AbstractApplicationContext#finishBeanFactoryInitialization}调用
 * ->14.1.{@link ProxyTransactionManagementConfiguration#transactionAdvisor()}: 注册事务增强器 BeanFactoryTransactionAttributeSourceAdvisor
 * ->14.2.{@link ProxyTransactionManagementConfiguration#transactionAttributeSource()}: 注入事务相关属性,如传播方式等
 * ->14.3.{@link ProxyTransactionManagementConfiguration#transactionInterceptor()}: 注入事务拦截器 {@link TransactionInterceptor}
 * {@link BeanFactoryTransactionAttributeSourceAdvisor}:会被{@link AbstractAutoProxyCreator#getAdvicesAndAdvisorsForBean}获取
 * 13.{@link TransactionInterceptor}:事务管理器,保存了事务属性信息,本身是一个 MethodInterceptor.
 * </pre>
 * 
 * Spring事务动态代理原理--运行过程中调用事务:
 * 
 * <pre>
 * 1.{@link TransactionInterceptor}:在目标方法执行的时候,会getAdvisors()获取拦截器链,并执行拦截器链,当只有事务拦截器时:
 * 		1.先获取事务相关的属性
 * 		2.再获取PlatformTransactionManager,如果没有指定Transactionmanger,会从容器中按类型获取一个PlatformTransactionManager
 * 		3.执行目标方法:如果正常,利用事务管理器提交事务;如果异常,获取到事务管理器,利用事务管理回滚操作
 * ->{@link TransactionAspectSupport#invokeWithinTransaction}:生成切面代理对象
 * {@link Transactional}:定义代理植入点,标识方法需要被代理,同时携带事务管理需要的一些属性信息,只对public方法有效
 * {@link TransactionDefinition}:定义事务的隔离级别,超时信息,传播行为,是否只读等信息
 * {@link TransactionStatus}:事务状态,根据事务定义信息进行事务管理,记录事务管理中的事务状态的对象
 * {@link AnnotationAwareAspectJAutoProxyCreator#postProcessAfterInitialization}:{@link BeanPostProcessor}的实现类,
 * 		主要是判断事务代理的植入点,返回一个代理对象给Spring容器.
 * {@link BeanFactoryTransactionAttributeSourceAdvisor}:在配置好注解驱动方式的事务管理之后,
 * 		Spring会在IOC容器创建一个该实例.这个实例可以看作是一个切点,在判断一个bean在初始化过程中是否需要创建代理对象时,
 * 		都需要验证一次 BeanFactoryTransactionAttributeSourceAdvisor 是否适用这个bean的切点.
 * 		如果适用于这个切点,就需要创建代理对象,并且把 BeanFactoryTransactionAttributeSourceAdvisor 实例注入到代理对象中
 * {@link AopUtils#findAdvisorsThatCanApply}:判断切面是否适用当前bean,可以在这个地方断点分析调用堆栈,
 * AopUtils#findAdvisorsThatCanApply一致调用,最终通过以下代码判断是否适用切点
 * {@link AbstractFallbackTransactionAttributeSource#computeTransactionAttribute}:targetClass就是目标class
 * 	{@link SpringTransactionAnnotationParser#parseTransactionAnnotation}:分析方法是否被 Transactional 标注,
 * 		如果有,BeanFactoryTransactionAttributeSourceAdvisor适配当前bean,进行代理,并注入切入点
 * ->{@link #JdkDynamicAopProxy#invoke}:默认为JDK代理实现事务
 * 		this.advised.getInterceptorsAndDynamicInterceptionAdvice():该方法返回 TransactionInterceptor
 * 		new ReflectiveMethodInvocation().proceed():最终调用{@link TransactionInterceptor#invoke}
 * ->{@link #CglibAopProxy.DynamicAdvisedInterceptor#intercept}:AOP最终的代理对象的代理方法
 * 		this.advised.getInterceptorsAndDynamicInterceptionAdvice():该方法返回 TransactionInterceptor
 * 		new CglibMethodInvocation().proceed():最终调用{@link TransactionInterceptor#invoke}
 * {@link TransactionInterceptor}:事务拦截器,实现了{@link MethodInterceptor},{@link Advice},在SpringBoot启动时注入
 * {@link TransactionInterceptor#invoke}:AOP切面最终调用的执行方法.
 * 		从调用链可以看到CglibMethodInvocation是包装了目标对象的方法调用的所有信息,因此,在该方法里面也可以调用目标方法,
 * 		并且还可以实现类似@Around的逻辑,在目标方法调用前后继续注入一些其他逻辑,比如事务管理逻辑
 * {@link TransactionAspectSupport#invokeWithinTransaction}:事务最终的调用方法,对开启了事务的方法和类进行主要操作
 * 		检查事务的传播机制,隔离级别,根据事务相关属性获得{@link TransactionManager},执行代理方法.
 * 		根据结果是否抛出异常,隔离机制等决定是否提交事务,回滚等
 * ->{@link TransactionAspectSupport#createTransactionIfNecessary}:开启事务
 * ->{@link TransactionAspectSupport#completeTransactionAfterThrowing}:回滚事务
 * ->{@link TransactionAspectSupport#commitTransactionAfterReturning}:提交事务
 * {@link PlatformTransactionManager}:Spring事务管理规范接口,由 TransactionAutoConfiguration 引入
 * ->{@link AbstractPlatformTransactionManager}:Spring用于管理事务的抽象类,实现基本的事务管理,但真正操作事务的在其子类中
 * -->{@link DataSourceTransactionManager}:实现真正的事务管理,包括事务开始,提交,暂停,回滚等
 * {@link AbstractFallbackTransactionAttributeSource#getTransactionAttribute()}:事务回滚主要方法,非public不回滚
 * </pre>
 * 
 * Spring事务失效:
 * 
 * <pre>
 * 1.当A()和B()在同一个类中,且A()调用B()时.由于Spring事务采用动态代理,当A()使用了事务时,
 * 		若B()开启了新事务,此时A()中调用B()使用的是this.B(),而不是由Spring的动态代理调用的B(),此时B()的事务不生效
 * 2.开启了多个数据库的事务
 * </pre>
 * 
 * {@link Transactional}:Spring AOP检查事务的注解标志,只对public方法有效
 * 
 * <pre>
 * {@link Transactional#value()}:事务管理类,根据环境使用{@link PlatformTransactionManager}或{@link ReactiveTransactionManager}
 * {@link Transactional#propagation()}:事务传播级别,默认是{@link Propagation.REQUIRED}
 * {@link Transactional#isolation()}:事务隔离级别,默认使用数据库默认级别
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class SingleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SingleApplication.class, args);
	}
}