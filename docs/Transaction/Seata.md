# Seata



# 概述



* 阿里的分布式事务框架,支持AT,XA,TCC等,详见官网
* Seata主要由三个重要组件组成
  * TC:Transaction Coordinator,事务协调器,管理全局的分支事务的状态,用于全局事务的提交和回滚
  * TM:Transaction Manager,事务管理器,用于开启,提交或者回滚全局事务
  * RM:Resource Manager,资源管理器,用于分支事务上的资源管理,向TC注册分支事务,上报分支事务的状态,接受TC的命令来提交或者回滚分支事务




# 安装



* 每一个微服务必须创建undo.log,安装事务协调器:seate-server

 * 导入依赖,解压并启动seata-server

 * registry.conf:注册中心配置

   ```json
   registry {
   	type = "nacos"
       nacos {
           serverAddr = "localhost"
           namespace = "public"
           cluster = "default"
       }
   }
   
   config {
       type = "nacos"
       nacos {
           serverAddr = "localhost"
           namespace = "public"
           cluster = "default"
       }
   }
   ```

 * 所有想要用到分布式事务的微服务使用seata的DataSourceProxyConfig代理自己的数据源

   ```java
   @Configuration
   public class DataSourceProxyConfig {
       @Bean
       @ConfigurationProperties(prefix = "spring.datasource")
       public DruidDataSource druidDataSource() {
           return new DruidDataSource();
       }
   
       @Primary
       @Bean
       public DataSourceProxy dataSource(DruidDataSource druidDataSource) {
           return new DataSourceProxy(druidDataSource);
       }
   }
   ```

 * 每个微服务的bootstrap.yml都需要配置seata全局事务组

   ```yaml
   spring:
   	application:
   		name: service-product
   	cloud:
   		nacos:
   			config:
   				server-addr: localhost:8848 # nacos的服务端地址
   				namespace: public
   				group: SEATA_GROUP
           alibaba:
               seata:
                   tx-service-group: ${spring.application.name}
   ```

 * 在微服务中开启全局事务:`@GlobalTransactional`

 * 每个微服务都必须导入registry.conf,file.conf,vgroup_mapping.{application.name}-fescar-server-group="default"

 * 启动:seata-server.sh -p 8090 -m file



# 事务流程



![](TX03.PNG)

* TM向TC申请开启一个全局事务,TC创建全局事务后返回全局唯一XID,XID会在全局事务上下文传播
* RM向TC注册分支事务,该分支事务归属于拥有相同XID的全局事务
* 各个RM执行相应的分支事务
* TM向TC发起全局提交或回滚
* TC调度XID下的分支事务完成提交或回滚



# AT



## 概述



![](XA00.png)

 * AT模式是基于XA事务演进而来的一个分布式事务中间件,XA是一个基于数据库实现的分布式事务协议,本质上和两阶段提交一样,需要数据库支持,Mysql5.6以上版本支持XA协议
 * 一种无侵入的分布式事务解决方案,2PC的广义实现
 * 编程模型不变,轻依赖不需要为分布式事务场景做特定设计
 * 一阶段提交,不阻塞,连接释放,保证整个系统的吞吐
 * Transaction Coordinator(TC):事务协调器,它是独立的中间件,需要独立部署运行,它维护全局事务的运行状态,接收TM指令发起全局事务的提交与回滚,负责与RM通信协调各各分支事务的提交或回滚
 * Transaction Manager(TM):事务管理器,TM需要嵌入应用程序中工作,负责开启一个全局事务,并最终向TC发起全局提交或全局回滚的指令
 * Resource Manager(RM):控制分支事务,负责分支注册,状态汇报,并接收事务协调器的指令,驱动分支(本地)事务的提交和回滚
 * Seata有一个重要的机制:**回滚日志**
    * 每个分支事务对应的数据库中都需要有一个回滚日志表 UNDO_LOG,在真正修改数据库记录之前,都会先记录修改前的记录值,以便之后回滚
    * 在收到回滚请求后,会根据 UNDO_LOG 生成回滚操作的 SQL 语句来执行
    * 如果收到的是提交请求,就把 UNDO_LOG 中的相应记录删除掉
 * RM是通过**监控拦截JDBC**实现自动和 TC 交互的.例如监控到开启本地事务了,就会自动向TC注册、生成回滚日志、向TC汇报执行结果
 * 如果二阶段回滚失败,例如有某个微服务挂掉了,那么所有正常的微服务也都不会执行回滚,当这个微服务重新正常运行后,TC 会重新执行全局回滚



## 事务流程



* 每个RM使用DataSourceProxy连接数据库,其目的是使用ConnectionProxy,使用数据源和数据连接代理的目的就是在第一阶段将undo_log日志和业务数据放在一个本地事务提交,这样就保证了只要有业务操作就一定有undo_log
* 在第一阶段undo_log中存放了数据修改前和修改后的值,为事务回滚作好准备,所以第一阶段完成就已经将分支事务提交,也就释放了锁资源
* TM开启全局事务开始,将XID全局事务id放在事务上下文中,通过feign调用也将XID传入下游分支事务,每个分支事务将自己的Branch ID分支事务ID与XID关联
* 第二阶段全局事务提交,TC会通知各各分支参与者提交分支事务,在第一阶段就已经提交了分支事务,这里各各参与者只需要删除undo_log即可,并且可以异步执行,第二阶段很快可以完成
* 第二阶段全局事务回滚,TC会通知各各分支参与者回滚分支事务,通过 XID 和 Branch ID 找到相应的回滚日志,通过回滚日志生成反向的 SQL 并执行,以完成分支事务回滚到之前的状态,如果回滚失败则会重试回滚操作



## 第一阶段



* Seata的JDBC数据源代理通过对业务SQL的解析,把业务数据在更新前后的数据镜像组织成回滚日志,利用本地事务的ACID 特性,将业务数据的更新和回滚日志写入在同一个本地事务中提交.这样可以保证任何提交的业务数据的更新一定有相应的回滚日志存在
* 提交完成之后将生成后镜像,即更新后的数据保存为快照,用于事务的幂等,隔离等

![](XA01.png)

* 基于这样的机制,分支的本地事务便可以在全局事务的第一阶段提交,并马上释放本地事务锁定的资源,并且不占用数据库连接
* 这也是Seata和XA事务的不同之处,两阶段提交往往对资源的锁定需要持续到第二阶段实际的提交或者回滚,而有了回滚日志之后,可以在第一阶段释放对资源的锁定,降低了锁范围,提高效率,即使第二阶段发生异常需要回滚,只需找对undo.log中对应数据并反解析成sql来达到回滚目的
* 同时Seata通过代理数据源将业务sql的执行解析成undo.log来与业务数据的更新同时入库,达到了对业务无侵入的效果



## 第二阶段



* 如果决议是全局提交,此时分支事务在第一阶段已经完成提交,不需要特殊处理,只需要异步清理回滚日志即可,二阶段可以非常快速地完成

![](XA02.png)

* 如果决议是全局回滚,RM收到协调器发来的回滚请求,通过XID和Branch ID找到相应的回滚日志记录,**通过回滚记录生成反向的更新SQL并执行**,以完成分支的回滚

![](XA03.png)



## 提交成功



![](TX07.PNG)



## 回滚



![](TX08.png)

* 因为一阶段已经提交数据,如果二阶段发生异常需要回滚,此时可能发生事务问题
* 先需要用一阶段生成的新镜像和回滚时数据库的数据进行比对,如果数据一致,则进行回滚.如果数据不一致,需要根据实际情况进行更新操作



## 应用



![](Seata02.png)



* 一个商城购买商品的接口,需要同时调用3个微服务



## SpringCloud



![](XA04.png)

* 在SpringCloud的编程中,需要给分布式事务发起的入口标注@GlobalTransactional,每一个远程的小事务仍然适用@Transactional
* GlobalTransactionalInterceptor会拦截@GlobalTransactional注解的方法,生成XID,XID会在整个分布式事务中传递  
* 在程序中,业务接口(Business)需要通过注解来标识他是一个全局事务,此时该接口就是TM
* Business会请求 TC,说明自己要开启一个全局事务,TC 会生成一个全局事务ID(XID),并返回给业务接口
* 业务接口得到XID 后,开始调用微服务Storage
* Storage收到 XID,知道自己的事务属于这个全局事务,Storage 执行自己的业务逻辑,操作本地数据库
* Storage会把自己的事务注册到 TC,作为这个XID下面的一个**分支事务**,并且把自己的事务执行结果也告诉 TC.此时Storage的角色是RM(资源管理者),资源是指本地数据库
* Order、Account的执行逻辑与 Storage 一致
* 在各个微服务都执行完成后,TC可以知道XID下各个分支事务的执行结果,Business也就知道了
* Business如果发现各个微服务的本地事务都执行成功了,就请求 TC 对这个 XID 提交,否则回滚
* TC 收到请求后,向 XID 下的所有分支事务发起相应请求
* 各个微服务收到 TC 的请求后,执行相应指令,并把执行结果上报 TC



# TCC



![](TCC03.PNG)

* `@LocalTCC`:TCC事务注解,必须写在接口上
* `@TwoPhaseBusinessAction`:TCC服务参与者注解,配置在接口方法上,必须在`@LocalTCC`修饰的接口中使用
  * `name`:指定被调用的微服务名称
  * `commitMethod`:指定事务成功时的提交方法
  * `rollbackMethod`:指定事务失败时的回滚方法
* 其他同AT模式,也需要在具体的调用方法上加`@GlobalTransactional`和`@Trabsactional`



# 订单事务



![](seata01.png)



* 每个RM使用DataSourceProxy连接数据库,其目的是使用ConnectionProxy,使用数据源和数据连接代理的目的就是在第一阶段将undo_log和业务数据放在一个本地事务提交,这样就保存了只要有业务操作就一定有undo_log
* 在第一阶段undo_log中存放了数据修改前和修改后的值,为事务回滚作准备,所以第一阶段完成就已经将分支事务提交,也就释放了锁资源
* TM开启全局事务开始,将XID全局事务id放在事务上下文中,通过feign调用也将XID传入下游分支事务,每个分支事务将自己的Branch ID分支事务ID与XID关联
* 第二阶段全局事务提交, TC会通知各个分支参与者提交分支事务,在第一阶段就已经提交了分支事务,这里各个参与者只需要删除undo_log即可,并且可以异步执行,第二阶段很快可以完成
* 第二阶段全局事务回滚, TC会通知各个分支回滚分支事务,通过 XID 和 Branch ID 找到相应的回滚日志,通过回滚日志生成反向的 SQL 并执行,以完成分支事务回滚到之前的状态,如果回滚失败则会重试回滚操作
