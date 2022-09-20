# TransactionSingle



# PlatformTransactionManager



```java
public interface PlatformTransactionManager {
    // 获取事务状态信息
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    // 提交事务
    void commit(TransactionStatus status) throws TransactionException;

    // 事务回滚
    void rollback(TransactionStatus status) throws TransactionException;
}
```



# Transactional



* 该注解是Spring注解配置事务的核心注解,无论是注解驱动开发还是注解和XML混合开发都要使用到
* 该注解可以出现在接口上,类上和方法上,分别表明:
  * 接口上: 当前接口的所有实现类中重写接口的方法有事务支持
  * 类上: 当前类中所有public方法有事务支持
  * 方法上: 当前public方法有事务的支持
  * 优先级: 方法上>类上>接口上



```java
package org.springframework.transaction.annotation;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    // 指定事务管理器的唯一标识
    @AliasFor("transactionManager")
    String value() default "";
    // 指定事务管理器的唯一标识
    @AliasFor("value")
    String transactionManager() default "";
    // 标签,对应多个
    String[] label() default {};
    // 指定事务的传播行为
    Propagation propagation() default Propagation.REQUIRED;
    // 指定事务的隔离级别
    Isolation isolation() default Isolation.DEFAULT;
    // 指定事务的超时时间,默认-1永不超时,但是可能连接超时
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
    // 指定事务的超时时间字符串,默认-1永不超时,但是可能连接超时
    String timeoutString() default "";
    // 指定事务是否只读
    boolean readOnly() default false;
    // 通过指定异常类的字节码,限定事务在特定情况下回滚
    Class<? extends Throwable>[] rollbackFor() default {};
    // 通过指定异常类的全限定类名,限定事务在特定情况下回滚
    String[] rollbackForClassName() default {};
    // 通过指定异常类的字节码,限定事务在特定情况下不回滚
    Class<? extends Throwable>[] noRollbackFor() default {};
    // 通过指定异常类的全限定类名,限定事务在特定情况下不回滚
    String[] noRollbackForClassName() default {};
}
```

  