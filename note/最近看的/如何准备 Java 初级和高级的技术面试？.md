> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/SE1CCiFjrb0m_BO7z5_-Xg

![](https://mmbiz.qpic.cn/mmbiz_jpg/TNUwKhV0JpSVRPjeSLaJl9CTlIX2hLuGxthoqgic3Iy5xz7khMEarhic4mHJBBSQlUclNUhORv43kPV6k9ja2Qgg/640?wx_fmt=jpeg)

> _作者：hsm_computer_
> 
> _来自：cnblogs.com/JavaArchitect/p/9032323.html_

本人最近几年一直在做 java 后端方面的技术面试官，而在最近两周，又密集了面试了一些 java 初级和高级开发的候选人，在面试过程中，我自认为比较慎重，遇到问题回答不好的候选人，我总会再三从不同方面提问，只有当反复确认能力不行才会下结论。

相反，如果候选人给我的印象不错，我也会从多个角度来衡量，以免招进会说但不会干活的 “大忽悠”。

其实倒也不是我故意要为难候选人，毕竟入职后就是同事，但面试官的职责使然，而且，如果资深的面试官一般也这样。

写到这里，恐怕会吓到一些想要面试的朋友，能力强和能力弱都会被多问，那怎么办？

这就是本文将要讲到的主题：**如何准备 Java 初级和高级的技术面试。**

#### **一. 换位思考下，如果你是面试官，你会怎么做**

1\. 只能通过简历和面试来衡量，别无他法。如果某位大牛确认能力很行，但面试时无法充分地自证能力，那对不起了，过不了，现实就这样。

2\. 如果面试官由于能力不行，招进来一个大忽悠，那估计会被领导骂。而且再也不会被让面试了，给领导的印象就不好了。所以不能评主观印象，而是会有些客观标准，具体而言，就是从多个方面问些题目，答好答坏就看候选人的。

![](https://mmbiz.qpic.cn/mmbiz_png/TNUwKhV0JpSVRPjeSLaJl9CTlIX2hLuGMd0soo9Z13f166Y9gMN0VuTC8D0HKycJYRllzdyVpjKZwh1CTC2BicQ/640?wx_fmt=png)

其实一些题目都差不多，但不同能力的面试官问问题的切入点和渐进程度会不同，而且有经验的面试官会挖掘候选人的优势，并能从候选人的说辞中判断候选人是真懂还是忽悠。 

#### **二. 总体上说下准备面试的几个方面点**

记得之前考政治，某个大题 10 分，分 5 个点，每个点的标准答案不多，也就一两句话。比较取巧的做法是，涵盖点要全，每个点无需多说，但要说到点子上。相反，如果在某个点做得再多，其它点没覆盖到，只能拿这个点的分。

同理，在面试时，应当综合准备 java Core，数据库，框架，分布式等方面的题目。根据我面试的结果，我发现不少候选人走了弯路，他们或者干脆不准备，准备时可能方法不到位，单准备一个方面。比如只准备了算法题，在这方面回答很好，但其它方面就一无所知了。

所以说，没有所谓的一定能成功的面试秘籍，但有可以帮助提升成功率的准备方法。

![](https://mmbiz.qpic.cn/mmbiz_png/TNUwKhV0JpSVRPjeSLaJl9CTlIX2hLuGMpmaneXraGvorufTibWyeoF5CHTlicqtEMUI2R2Yf9JBvlwp2dLnOKdA/640?wx_fmt=png)

切记，面试前一定得准备，否则成功的可能性很低，准备时，得综合看各方面的点。至于每个点要到什么程度，后文会讲到。 

#### **三. 架构方面需要准备的点**

**初级开发而言，需要让面试官感觉出如下的要点。**

1\. 熟悉 SSM 架构，至少在项目里做过。

这个的说法是，介绍项目时，用一个业务流程来说 spring mvc 如何做的。

2\. 知道 Spring MVC 中的细节，比如 @Autowired 的用法，如何把 url 映射到 Controller 上，ModelAndView 对象返回的方式等。

3\. 最好结合项目的用法，说下你是怎么用 AOP，拦截器的，比如说可以通过拦截器拦截非法请求，怎么用 AOP 输出日志等。

4\. 关于 ORM 方面，不限用过哪种，但得知道一对一，一多多，多对多等的用法，以及 cascade 和 inverse 的用法。

5\. 最好知道声明式事务的做法。

**如果你要应聘高级开发，那在上述基础上，最好了解如下的知识点：**

*   Spring Bean 的周期

*   最好能通过阅读源代码，说下 IOC,AOP 以及 Spring MVC 的工作流程，推荐阅读：[史上最全 69 道 Spring 面试题和答案](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247486678&idx=1&sn=2a5e38e67c3d267d6c58d963adb24ccc&chksm=eb5389e0dc2400f6f6d2e0eaded591fcfc32575ca227e2cebcf39eec13ff8071a649e494f7d8&scene=21#wechat_redirect)

*   最好能结合反射，说下 IOC 等的实现原理

*   Spring Boot 和 Spring Cloud 的一些知识点

#### **四. 数据库方面需要准备的点**

不少候选人会看很多 SQL 的技巧，比如 select 该怎么写，insert 又该怎么写，但仅限于此，不会再准备其它的。

这样就很吃亏，因为面试官会认为，哪怕是初级开发，SQL 语句也该会写，所以这块不会多问，而会问如下方面的问题。

1\. 索引怎么建的，怎么用的？比如我建好了一个索引，在 where 语句里写 name like '123%'会不会走索引，怎么情况下不该建索引，哪些语句不会走索引。

2\. 除了索引之外，你有过哪些 SQL 优化方面的经验，比如分库分表，或通过执行计划查看 SQL 的优化点。这最好是能结合你做的项目实际来讲。

这里，我面试下来，大概有 70% 的候选人只知道基本 SQL 的写法，所以哪怕你是只有理论经验，会说一些优化点，也是非常有利的。

这块对于高级开发而言，更得了解优化方面的技能。推荐阅读： [37 个 MySQL 数据库小技巧，不看别后悔！](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247487061&idx=1&sn=0dfc867eb90bb9f79d45faf3d2a470ec&chksm=eb538b63dc2402754308364cae71988ed083c85fe6b26e01ce1dda5c183065d2199c9c3912c5&scene=21#wechat_redirect)

#### **五. Java Core 方面需要准备的点**

这块是基础，其实很多问的问题，候选人一定会在项目里用到，但很少能说好说全。

这块主要会从集合，多线程，异常处理流程以及 JVM 虚拟机这些方面来问。

**集合方面：**

1\. hashcode 有没有重写过？在什么场景下需要重写。如果可以，结合 hash 表的算法，说下 hashmap 的实现原理。

对于高级开发而言，最好通过 ConcurrentHashMap 来说明下并发方面的底层实现代码。

2\. ArrayList，LinkedList 的差别，比如一个基于数组，一个基于链表，它们均是线程不安全的，ArrayList 的扩容做法等。

**对于高级而言，最好看下底层的代码。**

3\. Set 如何实现防重的，比如 TreeSet 和 HashSet 等。

4\. Collection 的一些方法，比如比较方法，包装成线程安全的方法等。

5\. 可能有些面试官会问，如何通过 ArrayList 实现队列或堆栈，这个可以准备下。

**多线程方面，其实在项目里不怎么会用到，但会问如下的问题：**

1\. synchronized 和可重入锁的差别，然后可能会顺便问下信号量等防并发的机制。

2\. 在线程里该如何返回值，其实就是 callable runnable 区别。

3\. 一定得通过 ThreadLocal 或 volatile 关键字，来说明线程的内存模型。

4\. 线程池方面，会用，了解些常用参数

线程方面，可能问得比较多的就是并发机制，如果是高级开发，可能会问得深些。

**虚拟机方面**

1\. 结构图和流程可以大致说下。

2\. 一定得了解针对堆的垃圾回收机制，具体而言，可以画个图，说下年轻代年老代等。

3\. 说下垃圾回收的流程，然后针对性地说下如何在代码中优化内存性能。

4\. 最好说下如果出现了 OOM 异常，该怎么排查？如何看 Dump 文件。

5\. GC 的一些概念，比如强弱软引用，finalize 方法等，这些可以准备下。 在 Java 技术栈微信公众号后台回复：Java，可以阅读 Java 核心技术知识点。

#### **六. 算法，设计模式等，其实是虚的**

这块好准备，不过话说哪怕这些没回答好，但能证明有相关技能的项目经验，一般也会让过。   

不过在这块，不少候选人就本末倒置了，比如就准备算法，设计模式，刚才提到的框架，数据库和 Java Core 方面就不准备了。这样很吃亏，就好比考政治只复习了一个点，其它一点也不准备。    

#### **七. 我面试的感受 & 听到哪类回答就能证明候选人比较资深**

1\. 大多数的候选人（大概 7 成）直接就来了，不做任何准备。要知道，面试和项目其实有些脱节，哪怕项目做得再好，不做准备照样通不过，只要我确认过这类人确实无法达标，我拒掉他们没任何心理负担，谁让他们不准备？

2\. 还有些候选人态度很好，明显准备过，但没准备到位，比如像刚才所说，只准备了算法，或者在 Java Core 方面，只看了集合方面的面试题。对于这些同学，哪怕是过了，我也会感到惋惜，毕竟如果面试好些的话，工资也能更高些，至于哪些过不了的，我敢说，如果他们准备过，估计就不是这个结果了。

其实我也知道，人无完人，哪怕我自己去面试，也不可能面面俱到，所以，我不会要求候选人什么问题都能回答出，甚至大多答错也没关系，只要能证明自己的能力即可通过面试。

我也和不少面试官交流过，根据我们的经验，如果候选人能说出如下的知识点，即能证明他在这个领域比较资深了，在这块，我可能就不会过多地问问题了。  

**架构方面**

1\. 能证明自己可以干活（这不难），同时能结合底层代码说出 IOC，AOP 或 Spring MVC 的流程，只要能说出一个即可。或者能说出拦截器，Controller 等的高级用法。

2\. 能证明自己有 Spring Boot 或 Spring Cloud 的经验，比如能说出些 Spring Cloud 组件的用法。

3\. 如果能证明自己有分布式开发的经验，那最好了，其实这不难证明，比如能说出服务的包是放在多台机器上（大多数公司其实都这样），而且能说出如何部署，如何通过 nginx 等做到负载均衡。

数据库方面，其实讲清楚一个问题即可：如何进行 SQL 调优，比如通过索引，看执行计划即可，如果有其它的优化点，说清楚即可。

**Java Core 方面，这里给出些诀窍：** 

1\. 能结合 ConcurrentHashMap 的源代码，说出 final,volatile,transient 的用法，以及在其中如何用 Lock 对象防止写并发。

2\. 结合一个项目实际，说下设计模式的实践。

3\. 多线程方面，能说出 Lock 或 volatile 等高级知识点的用法。

4\. 这块最取巧：说下 GC 的流程，以及如何通过日志和 Dump 文件排查 OOM 异常，如果再高级些的话，说下如何在代码中优化内存代码。    

诀窍点归结成一个：能结合源代码或项目实际，说出些比较资深的问题。在 java 技术栈微信公众号后台回复：多线程，可以阅读 Java 多线程技术文章。推荐阅读：[史上最全 Java 多线程面试题及答案](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247486721&idx=2&sn=c2058d5ddd7453eb9c39732c114879d5&chksm=eb538837dc240121a7be7c64edf66ede63d84bb50f6b279764a365c76437cf7db0681ebec2b3&scene=21#wechat_redirect)

#### **八. 本文的侧重点**

本文的侧重点是：

1.  面试一定得准备 （重要的话说三遍，这里已经超过 3 遍了）

2.  如何全面充分地准备。

至于为什么要写这个文章？我得不停地总结我作为面试官的技巧，这样我在面试中也能更高效更准确地招到合适的人才。

**最近干货分享**

[过了所有技术面，却倒在 HR 一个问题上。](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247488067&idx=1&sn=b96a661c001c8161cc078f0f6b40e565&chksm=eb539775dc241e63dace4baa861e65f193d27d76299466afbf805a1f56c34e6823b779eb65cd&scene=21#wechat_redirect)

[运行 Spring Boot 应用的 3 种方式！](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247488078&idx=1&sn=69c34ffc4fa2a576460cdc00438abf49&chksm=eb539778dc241e6e8b76cd6bb045dbcef909cea97231948e08b9e4a0838f7986e164777fdbe6&scene=21#wechat_redirect)

[HashMap, ConcurrentHashMap 源码分析](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247487963&idx=1&sn=e2a492f26825457034476a85aa41db64&chksm=eb5394eddc241dfb269abf637e3fd841cf782034945970599449fbf1fdcb31d234574dc33d75&scene=21#wechat_redirect)

[阿里启动新项目：Nacos，比 Eureka 更强！](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247488083&idx=1&sn=75cbbb39c04510953e9d7b0eb8e43147&chksm=eb539765dc241e73849c188fd51761aeb09b2ea9b1f3919313659476bf5a92a764bd42828b73&scene=21#wechat_redirect)

[Redis 为什么这么快？](http://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247488079&idx=1&sn=1ecf7c491e9275dda8bfe0a52376cfe5&chksm=eb539779dc241e6fff288cd248f6d99c9456dea607fa7dfb624fc1597065e01bf4d2f92974fe&scene=21#wechat_redirect)

![](https://mmbiz.qpic.cn/mmbiz_jpg/TNUwKhV0JpQiar894HDic0yJSCHN19dmct0zTB1I3ib1uiaibB0XWIHOJdXWNmq4ZKHTVL6wS24ThQfY5ibqfqNKLung/640)

**好看 ↘**