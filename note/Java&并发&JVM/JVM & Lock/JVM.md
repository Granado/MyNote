> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483807&idx=1&sn=aa7a25fe8d4a731cfa0a3890cf100041&chksm=fd2fd05dca58594bd08fe37741fd86b5aea2c2a274f6853b3ba3f8492eaaa599a008586357bb&scene=21#wechat_redirect

在应聘 Java 程序员时，经常会被问到 JVM 相关的知识点。而市面上讲解 JVM 原理及结构的书籍及资料，要么晦涩难懂，要么断章取义。那么有没有一张比较清晰的图能够将 JVM 的整体轮廓有一个清晰的描述呢？

小码农特地为大家绘制了一张图，希望对大家有用！

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVutonvzXW2gTIfQIu7GvyH0hEgPp3za9DK303BJveCWOAPdHQIscDDfNP7hyAYzsrjWlJhhXKHGooVEg/640?wx_fmt=jpeg)

图中涉及的各垃圾回收器特点如下：

**1、Serial（新生代 - 串行 - 收集器）**

*   策略：标记 - 复制 - 清除；

*   优点：简单高效，适合 Client 模式的桌面应用（Eclipse）；

*   缺点：多核环境下，无法充分利用资源；

**2、parnew（新生代 - 并行 - 收集器）**

*   策略：标记 - 复制 - 清除；

*   优点：多线程、独占式，多核环境下提高 CPU 利用率；

*   缺点：单核环境下比 Serial 效率低；

**3、Parallel Scanvenge(新生代 - 并行 - 收集器)**

*   策略：标记 - 复制 - 清除；

*   优点：精准控制 “吞吐量”、gc 时间。吞吐量 = 执行用户代码时间 /(执行用户代码时间 + 内存回收时间)；

*   配置参数（可通过参数精准调控）：   

**4、Serial Old（老年代 - 串行 - 收集器）**

*   策略：标记 - 清除 - 整理；

*   优点：简单高效；

*   缺点：多核环境下，无法充分利用资源；

**5、Parall Old（老年代 - 并行 - 收集器）**

*   策略：标记 - 清除 - 整理；

*   优点：多核环境下，提高 CPU 利用率；

*   缺点：单核环境下，比 Serial Old 效率要低；

**6、CMS（老年代 - 并发 - 收集器）；**

*   策略：标记 - 清除；优点：“停顿时间” 最短；

*   缺点：内存碎片（有补偿策略）；

*   适用场景：互联网 Web 应用的 Server 端、涉及用户交互、响应速度快；

**7、G1（新生代 & 老年代 - 并行 & 并发 - 服务端收集器）**

*   策略：G1 将内存划分为 Region, 避免内存碎片；

*   优点：Eden、Survivor、Tenured 不再固定，内存使用率更高；可控的 STW 时间，根据预期的停顿时间，只回收部分 Region；

*   适应场景：多核 CPU，JVM 占用内存比较大的情况（>4GB）;

欢本文的朋友们，欢迎长按下图关注订阅号**「无敌码农」**，收看更多精彩内容

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVuton5KOpE65Uoh7kf3PZ9yRbtyBMAuSscgOFicWLHWWO8KBIibf8WeI7lqvrbr9SOs8wnVteDUwLg2YicA/640?wx_fmt=jpeg)