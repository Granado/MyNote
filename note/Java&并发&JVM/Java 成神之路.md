[《成神之路系列文章》](http://www.hollischuang.com/archives/1001) ，分章分节介绍所有知识点。

## 一、基础篇

### JVM

#### JVM 内存结构

堆、栈、方法区、直接内存、堆和栈区别

##### JVM内存布局：

**线程私有**：Java 方法栈（局部变量表，操作数栈，方法出口），Native 方法栈，程序计数器

**线程共有**：

1. 方法区（加载的类信息、静态变量、final类型的常量、属性和方法信息，JDK1.8之后不放入由JVM管理的堆，放入直接内存，1.8之前在永久代）

2. 直接内存（UnSafe.allocateMemory）

3. Java 堆（新生代：Eden + survisor 0 + survisor 1，标记复制法，98%的新对象会被回收。 老年代：标记整理法）。

**内存有关的：**

GC有关：SoftReference，WeakReference，PhantomReference ，System.GC()

内存有关的配置参数：-Xms 最小，-Xmx 最大，-XX:NewRatio=old/young 新老代的内存分配比例，-XX:SurvisorRatio=Eden/Survisor 拯救区的比例

#### Java 内存模型

内存可见性、重排序、顺序一致性、volatile、锁、final

#### 垃圾回收

内存分配策略、垃圾收集器（G1）、GC 算法、GC 参数、对象存活的判定

#### JVM 参数及调优

#### Java 对象模型

oop-klass、对象头

#### HotSpot

即时编译器、编译优化

#### 类加载机制

classLoader、类加载过程、双亲委派（破坏双亲委派）、模块化（jboss modules、osgi、jigsaw）

#### 虚拟机性能监控与故障处理工具

jps, jstack, jmap、jstat, jconsole, jinfo, jhat, javap, btrace、TProfiler

### 编译与反编译

javac 、javap 、jad 、CRF

### Java 基础知识

#### 阅读源代码

String、Integer、Long、Enum、BigDecimal、ThreadLocal、ClassLoader & URLClassLoader、ArrayList & LinkedList、 HashMap & LinkedHashMap & TreeMap & CouncurrentHashMap、HashSet & LinkedHashSet & TreeSet

#### Java 中各种变量类型

#### 熟悉 Java String 的使用，熟悉 String 的各种函数

JDK 6 和 JDK 7 中 substring 的原理及区别、

replaceFirst、replaceAll、replace 区别、

String 对 “+” 的重载、

String.valueOf 和 Integer.toString 的区别、

字符串的不可变性

#### 自动拆装箱

Integer 的缓存机制

#### 熟悉 Java 中各种关键字

transient、instanceof、volatile、synchronized、final、static、const 原理及用法。

#### 集合类

常用集合类的使用、ArrayList 和 LinkedList 和 Vector 的区别 、SynchronizedList 和 Vector 的区别、HashMap、HashTable、ConcurrentHashMap 区别、Java 8 中 stream 相关用法、apache 集合处理工具类的使用、不同版本的 JDK 中 HashMap 的实现的区别以及原因

#### 枚举

枚举的用法、枚举与单例、Enum 类

#### Java IO&Java NIO，并学会使用

bio、nio 和 aio 的区别、三种 IO 的用法与原理、netty

#### Java 反射与 javassist

反射与工厂模式、 `java.lang.reflect.*`

#### Java 序列化

什么是序列化与反序列化、为什么序列化、序列化底层原理、序列化与单例模式、protobuf、为什么说序列化并不安全

#### 注解

元注解、自定义注解、Java 中常用注解使用、注解与反射的结合

#### JMS

什么是 Java 消息服务、JMS 消息传送模型

#### JMX

`java.lang.management.*`、 `javax.management.*`

#### 泛型

泛型与继承、类型擦除、泛型中 K T V E ？ object 等的含义、泛型各种用法

#### 单元测试

junit、mock、mockito、内存数据库（h2）

#### 正则表达式

`java.lang.util.regex.*`

#### 常用的 Java 工具库

`commons.lang`, `commons.*...` `guava-libraries` `netty`

#### 什么是 API&SPI

#### 异常

异常类型、正确处理异常、自定义异常

#### 时间处理

LocalDate，LocalTime，LocalDateTime

时区、时令、Java 中时间 API

#### 编码

Java内置编码 UTF16 ，JDK1.8 和 JAVA9以上，String的实现（final char[] 改为 final byte[]）。

Java 9 以上，String 默认 compact 模式，并且只有 Latin1 和 UTF16 两种编码方式，如果不存在任何的UTF16的字符，那么就会压缩存储为 Latin1。

解决乱码问题、常用编码方式

#### 语法糖

Java 中语法糖原理、解语法糖

### Java 并发编程

#### 源码

Thread、Runnable、Callable、Atomic*、ConcurrentHashMap、Executors，ThreadPoolExecutor

锁相关，同步相关：ReentrantLock、ReentrantReadWriteLock、Semaphore、CountDownLatch、CyclicBarrier

#### Java 线程的状态

```java
// A thread that has not yet started is in this state.
Thread.State.NEW; // new Thread(() -> {...})

// A thread executing in the Java virtual machine is in this state.
Thread.State.RUNNABLE; // new Thread(() -> {...}).start();

// A thread that is blocked waiting for a monitor lock is in this state.
Thread.State.BLOCKED; // synchronized (obj) {...}

// A thread that is waiting indefinitely for another thread to perform a particular action is in this state.
Thread.State.WAITING; // obj.wait(); condition.await(); reentrantLock.lock(); 

// A thread that is waiting for another thread to perform an action for up to a specified waiting time is in this state.
Thread.State.TIMED_WAITING; // obj.wait(time); condition.await(time)

// A thread that has exited is in this state.
Thread.State.TERMINATED;

// 初始状态为 NEW ，中止状态为 TERMINATED
// BLOCKED 和 WAITING 的区别在于 BLOCKED 是等待锁，WAITING 是线程间都已经拿到过锁了，
// 但是在执行的时候通过 wait() 方法放弃时间片，等待其他线程执行。
// Java中，IO阻塞时，线程处于 Runable 状态，Runable 状态包含了操作系统 Ready, Running, Waiting 三个状态。
```

#### 线程池

Executors.newFixedThreadPool(int) 返回固定线程的线程池

Executors.newWorkStealingPool(int parallelism)  创建JoinForkPool，并指定并发度，默认为CPU核心数的线程

Executors.newSingleThreadExecutor() 创建单线程的线程池

Executors.newCachedThreadPool() 创建带缓存的线程池，默认缓存1分钟，如果频繁添加Task，就创建线程，如果有空闲的缓存线程，就是用缓存线程

Executors.newScheduledThreadPool() 创建一个可以调度的线程池

**线程池的拒绝策略**：

排队策略：

1. 直接提交。直接提交策略表示线程池不对任务进行缓存。新进任务直接提交给线程池，当线程池中没有空闲线程时，创建一个新的线程处理此任务。这种策略需要线程池具有无限增长的可能性。实现为：SynchronousQueue
2. 有界队列。当线程池中线程达到corePoolSize时，新进任务被放在队列里排队等待处理。有界队列（如ArrayBlockingQueue）有助于防止资源耗尽，但是可能较难调整和控制。队列大小和最大池大小可能需要相互折衷：使用大型队列和小型池可以最大限度地降低 CPU 使用率、操作系统资源和上下文切换开销，但是可能导致人工降低吞吐量。如果任务频繁阻塞（例如，如果它们是 I/O 边界），则系统可能为超过您许可的更多线程安排时间。使用小型队列通常要求较大的池大小，CPU 使用率较高，但是可能遇到不可接受的调度开销，这样也会降低吞吐量。
3. 无界队列。使用无界队列（例如，不具有预定义容量的 LinkedBlockingQueue）将导致在所有 corePoolSize 线程都忙时新任务在队列中等待。这样，创建的线程就不会超过 corePoolSize。（因此，maximumPoolSize 的值也就无效了。）当每个任务完全独立于其他任务，即任务执行互不影响时，适合于使用无界队列；例如，在 Web 页服务器中。这种排队可用于处理瞬态突发请求，当命令以超过队列所能处理的平均数连续到达时，此策略允许无界线程具有增长的可能性。


```reStructuredText
  Proceed in 3 steps:
  1. If fewer than corePoolSize threads are running, try to
  start a new thread with the given command as its first
  task.  The call to addWorker atomically checks runState and
  workerCount, and so prevents false alarms that would add
  threads when it shouldn't, by returning false.
 
  2. If a task can be successfully queued, then we still need
  to double-check whether we should have added a thread
  (because existing ones died since last checking) or that
  the pool shut down since entry into this method. So we
  recheck state and if necessary roll back the enqueuing if
  stopped, or start a new thread if there are none.
 
  3. If we cannot queue task, then we try to add a new
  thread.  If it fails, we know we are shut down or saturated
  and so reject the task.															
```
拒绝策略：当任务源源不断的过来，而我们的系统又处理不过来的时候，我们要采取的策略是拒绝服务。RejectedExecutionHandler接口提供了拒绝任务处理的自定义方法的机会。在ThreadPoolExecutor中已经包含四种处理策略。

1. CallerRunsPolicy：线程调用运行该任务的 execute 本身。此策略提供简单的反馈控制机制，能够减缓新任务的提交速度。 
   public void rejectedExecution(Runnable r, ThreadPoolExecutor e) { if (!e.isShutdown()) { r.run(); }} 
   这个策略显然不想放弃执行任务。但是由于池中已经没有任何资源了，那么就直接使用调用该execute的线程本身来执行。（开始我总不想丢弃任务的执行，但是对某些应用场景来讲，很有可能造成当前线程也被阻塞。如果所有线程都是不能执行的，很可能导致程序没法继续跑了。需要视业务情景而定吧。）
2. AbortPolicy：处理程序遭到拒绝将抛出运行时 RejectedExecutionException 
   public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {throw new RejectedExecutionException();} 
   这种策略直接抛出异常，丢弃任务。（jdk默认策略，队列满并线程满时直接拒绝添加新任务，并抛出异常，所以说有时候放弃也是一种勇气，为了保证后续任务的正常进行，丢弃一些也是可以接收的，记得做好记录）
3. DiscardPolicy：不能执行的任务将被删除 
   public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {} 
   这种策略和AbortPolicy几乎一样，也是丢弃任务，只不过他不抛出异常。
4. DiscardOldestPolicy：如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程） 
   该策略就稍微复杂一些，在pool没有关闭的前提下首先丢掉缓存在队列中的最早的任务，然后重新尝试运行该任务。这个策略需要适当小心。

submit() 返回 Future 来感知异步完成

execute() 只管执行 task

invokeAll() 返回 List<Future>

#### 线程安全

死锁、死锁如何排查、Java 线程调度、线程安全和内存模型的关系

#### 锁

CAS、乐观锁与悲观锁、数据库相关锁机制、分布式锁、偏向锁、轻量级锁、重量级锁、monitor、锁优化、锁消除、锁粗化、自旋锁、可重入锁、阻塞锁、死锁

#### 死锁

#### volatile

happens-before、编译器指令重排和 CPU 指令重

#### synchronized

synchronized 是如何实现的？synchronized 和 lock 之间关系、不使用 synchronized 如何实现一个线程安全的单例

#### sleep 和 wait

#### wait 和 notify

#### notify 和 notifyAll

#### ThreadLocal

#### 写一个死锁的程序

#### 写代码来解决生产者消费者问题

#### 守护线程

守护线程和非守护线程的区别以及用法

## 二、 进阶篇

### Java 底层知识

#### 字节码、class 文件格式

#### CPU 缓存，L1，L2，L3 和伪共享

#### 尾递归

#### 位运算

用位运算实现加、减、乘、除、取余

### 设计模式

#### 了解 23 种设计模式

#### 会使用常用设计模式

单例、策略、工厂、适配器、责任链。

#### 实现 AOP

#### 实现 IOC

#### 不用 synchronized 和 lock，实现线程安全的单例模式

#### nio 和 reactor 设计模式

### 网络编程知识

#### tcp、udp、http、https 等常用协议

三次握手与四次关闭、流量控制和拥塞控制、OSI 七层模型、tcp 粘包与拆包

#### http/1.0 http/1.1 http/2 之前的区别

#### Java RMI，Socket，HttpClient

#### cookie 与 session

cookie 被禁用，如何实现 session

#### 用 Java 写一个简单的静态文件的 HTTP 服务器

> 实现客户端缓存功能，支持返回 304 实现可并发下载一个文件 使用线程池处理客户端请求 使用 nio 处理客户端请求 支持简单的 rewrite 规则 上述功能在实现的时候需要满足 “开闭原则”

#### 了解 nginx 和 apache 服务器的特性并搭建一个对应的服务器

#### 用 Java 实现 FTP、SMTP 协议

#### 进程间通讯的方式

#### 什么是 CDN？如果实现？

#### 什么是 DNS？

#### 反向代理

### 框架知识

#### Servlet 线程安全问题

#### Servlet 中的 filter 和 listener

#### Hibernate 的缓存机制

#### Hiberate 的懒加载

#### Spring Bean 的初始化

#### Spring 的 AOP 原理

#### 自己实现 Spring 的 IOC

#### Spring MVC

#### Spring Boot2.0

Spring Boot 的 starter 原理，自己实现一个 starter

#### Spring Security

### 应用服务器知识

#### JBoss

#### tomcat

#### jetty

#### Weblogic

### 工具

#### git & svn

#### maven & gradle

## 三、 高级篇

### 新技术

#### Java 8

lambda 表达式、Stream API、

#### Java 9

Jigsaw、Jshell、Reactive Streams

#### Java 10

局部变量类型推断、G1 的并行 Full GC、ThreadLocal 握手机制

#### Spring 5

响应式编程

#### Spring Boot 2.0

### 性能优化

使用单例、使用 Future 模式、使用线程池、选择就绪、减少上下文切换、减少锁粒度、数据压缩、结果缓存

### 线上问题分析

#### dump 获取

线程 Dump、内存 Dump、gc 情况

#### dump 分析

分析死锁、分析内存泄露

#### 自己编写各种 outofmemory，stackoverflow 程序

HeapOutOfMemory、 Young OutOfMemory、MethodArea OutOfMemory、ConstantPool OutOfMemory、DirectMemory OutOfMemory、Stack OutOfMemory Stack OverFlow

#### 常见问题解决思路

内存溢出、线程死锁、类加载冲突

#### 使用工具尝试解决以下问题，并写下总结

当一个 Java 程序响应很慢时如何查找问题、

当一个 Java 程序频繁 FullGC 时如何解决问题、

如何查看垃圾回收日志、

当一个 Java 应用发生 OutOfMemory 时该如何解决、

如何判断是否出现死锁、

如何判断是否存在内存泄露

### 编译原理知识

#### 编译与反编译

#### Java 代码的编译与反编译

#### Java 的反编译工具

#### 词法分析，语法分析（LL 算法，递归下降算法，LR 算法），语义分析，运行时环境，中间代码，代码生成，代码优化

### 操作系统知识

#### Linux 的常用命令

#### 进程同步

#### 缓冲区溢出

#### 分段和分页

#### 虚拟内存与主存

### 数据库知识

#### MySql 执行引擎

#### MySQL 执行计划

如何查看执行计划，如何根据执行计划进行 SQL 优化

#### SQL 优化

#### 事务

事务的隔离级别、事务能不能实现锁的功能

#### 数据库锁

行锁、表锁、使用数据库锁实现乐观锁、

#### 数据库主备搭建

#### binlog

#### 内存数据库

h2

#### 常用的 nosql 数据库

redis、memcached

#### 分别使用数据库锁、NoSql 实现分布式锁

#### 性能调优

### 数据结构与算法知识

#### 简单的数据结构

栈、队列、链表、数组、哈希表、

#### 树

二叉树、字典树、平衡树、排序树、B 树、B + 树、R 树、多路树、红黑树

#### 排序算法

各种排序算法和时间复杂度 深度优先和广度优先搜索 全排列、贪心算法、KMP 算法、hash 算法、海量数据处理

### 大数据知识

#### Zookeeper

基本概念、常见用法

#### Solr，Lucene，ElasticSearch

在 linux 上部署 solr，solrcloud，，新增、删除、查询索引

#### Storm，流式计算，了解 Spark，S4

在 linux 上部署 storm，用 zookeeper 做协调，运行 storm hello world，local 和 remote 模式运行调试 storm topology。

#### Hadoop，离线计算

HDFS、MapReduce

#### 分布式日志收集 flume，kafka，logstash

#### 数据挖掘，mahout

### 网络安全知识

#### 什么是 XSS

XSS 的防御

#### 什么是 CSRF

#### 什么是注入攻击

SQL 注入、XML 注入、CRLF 注入

#### 什么是文件上传漏洞

#### 加密与解密

MD5，SHA1、DES、AES、RSA、DSA

#### 什么是 DOS 攻击和 DDOS 攻击

memcached 为什么可以导致 DDos 攻击、什么是反射型 DDoS

#### SSL、TLS，HTTPS

#### 如何通过 Hash 碰撞进行 DOS 攻击

#### 用 openssl 签一个证书部署到 apache 或 nginx

## 四、架构篇

### 分布式

数据一致性、服务治理、服务降级

#### 分布式事务

2PC、3PC、CAP、BASE、 可靠消息最终一致性、最大努力通知、TCC

#### Dubbo

服务注册、服务发现，服务治理

#### 分布式数据库

怎样打造一个分布式数据库、什么时候需要分布式数据库、mycat、otter、HBase

#### 分布式文件系统

mfs、fastdfs

#### 分布式缓存

缓存一致性、缓存命中率、缓存冗余

### 微服务

SOA、康威定律

#### ServiceMesh

#### Docker & Kubernets

#### Spring Boot

#### Spring Cloud

### 高并发

#### 分库分表

#### CDN 技术

#### 消息队列

ActiveMQ

### 监控

#### 监控什么

CPU、内存、磁盘 I/O、网络 I/O 等

#### 监控手段

进程监控、语义监控、机器资源监控、数据波动

#### 监控数据采集

日志、埋点

#### Dapper

### 负载均衡

tomcat 负载均衡、Nginx 负载均衡

### DNS

DNS 原理、DNS 的设计

### CDN

数据一致性

## 五、 扩展篇

### 云计算

IaaS、SaaS、PaaS、虚拟化技术、openstack、Serverlsess

### 搜索引擎

Solr、Lucene、Nutch、Elasticsearch

### 权限管理

Shiro

### 区块链

哈希算法、Merkle 树、公钥密码算法、共识算法、Raft 协议、Paxos 算法与 Raft 算法、拜占庭问题与算法、消息认证码与数字签名

#### 比特币

挖矿、共识机制、闪电网络、侧链、热点问题、分叉

#### 以太坊

#### 超级账本

### 人工智能

数学基础、机器学习、人工神经网络、深度学习、应用场景。

#### 常用框架

TensorFlow、DeepLearning4J

### 其他语言

Groovy、Python、Go、NodeJs、Swift、Rust

## 六、 推荐书籍

《深入理解 Java 虚拟机》 《Effective Java》 《深入分析 Java Web 技术内幕》 《大型网站技术架构》 《代码整洁之道》 《Head First 设计模式》 《maven 实战》 《区块链原理、设计与应用》 《Java 并发编程实战》 《鸟哥的 Linux 私房菜》 《从 Paxos 到 Zookeeper》 《架构即未来》

</article>