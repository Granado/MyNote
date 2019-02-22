> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483820&idx=1&sn=8418f0f6a618bb0f0ca0980af09a816f&chksm=fd2fd06eca5859786ab124dd204a7ec9b1ad3ed230b9b531086cc6729a277a05d3e8307b7e0d&scene=21#wechat_redirect

> 在上一版文章发出后，作者收到了很多朋友的反馈，有反馈图片不清晰的，也有反馈说内存部分画的不是太细、缺少必要的文字描述，在这里小码农要向这些朋友表示抱歉，同时也向这些朋友表示感谢，正是因为有了你们的鞭策，小码农才有了持续学习的动力。
> 
> 所以，这两天小码阅读了更为详细的资料，并对之前的内容进行了更为细化的梳理，希望这次能让大家对 JVM 相关的知识点有更加深刻的理解，也欢迎大家多多批评指正。

**JVM 结构示意图**

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutolu35tGfEL6Noe6QgyicDnsPuicSjm60hvn5B1bEvqmNic8eD3FhbqcicGk17Psia6h6YGpCaS8g7kWicag/640?wx_fmt=png)

**JVM 总体概述** 

JVM 总体上是由类装载子系统（ClassLoader）、运行时数据区、执行引擎、内存回收这四个部分组成。其中我们最为关注的运行时数据区，也就是 JVM 的内存部分则是由方法区（Method Area）、JAVA 堆（Heap）、虚拟机栈（Stack）、程序计数器、本地方法栈这几部分组成；除此以外，在概念中还有一个直接内存的概念，事实上这部分内存并不属于虚拟机规范中定义的内存区域，但是因为在 JDK1.4 + 后新加的 NIO 类，以及 JDK1.8 + 后的 Metaspace 的关系，所以在讨论 JVM 时也经常会被放到一起讨论。

**JVM 内存概述** 

各内存部分的功能及具体组成部分，总结如下：

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVutokVL8r9J3zTicCRNSC2EXwW2qVeDoo1K6MptMZQib00C7MydayrPcNhonxArupYPey9PMZAYrdYm13g/640?wx_fmt=jpeg)

需要说明的是，堆内存是 GC 重点回收区域，其中分代回收机制将堆内存划分为年轻代、老年代两个区域，默认情况下年轻代占整个堆内存空间的 1/3, 而老年代则占 2/3, 可以通过 “-XX:NewRatio” 设置年轻代与老年代的比值，默认为 2，表示比值年轻代与老年代的比值为“1：2”，在 JVM 调优时可根据应用实际情况进行调整。

而年轻代又分为 Eden、Survivor0、Survivor1，这三个区域占整个新生代空间的比值为 8:1:1，即 Eden 区占 8/10，其他两个区域分别占 1/10, 可通过 “-XX:SurvivorRatio” 参数进行设置，默认值为 8。

**正确理解并发问题**

在了解了 JVM 结构，特别是内存结构后，我们再说说并发问题产生的原因。在上面的内容中我们分析了 Java 堆、Java 栈，知道 Java 堆存储的是对象，而 Java 栈内存是方法执行时所需要的局部变量，其中就包括堆中对象的引用，如果多个线程同时修改堆中同一引用对象的数据，就可能会产生并发问题，导致多个线程中某些线程得到的数据值与实际值不符，造成脏数据。

**那么这种问题为什么会发生呢？**

实际上，线程操作堆中共享对象数据时并不是直接操作对象所在的那块内存，这里称之为主内存；而是将对象拷贝到线程私有的工作内存中进行更新，完成后再将最新的数据值同步回主内存，而多个线程在同一时刻将一个对象的值改得七七八八，然后再同时同步给对象所在的内存区域，那么以谁更新的为准就成了问题了。

所以，为了防止这种情况出现，Java 提供了同步机制，确保各个线程按照一定的机制同一时刻只能运行一个线程更新主内存的值。

具体逻辑示意图如下：

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVutokVL8r9J3zTicCRNSC2EXwW2F6ss7G6AJGv9KVlRkarLMEJeLUiaD1eBbHBXCmYsGctM32hCPKfrhiag/640?wx_fmt=jpeg)

注意，这里所讲的主内存、工作内存与 Java 内存区域中的 Java 堆、栈内存、方法区等并不是同一个层次的内存划分。如果勉强类比，从变量、主内存、工作内存的定义来看，主内存主要对应于 Java 堆中对象实例数据部分，而工作内存则对应于虚拟机栈中使用的部分内存区域；从更低层次类比，主内存就直接对应于物理硬件的内存，而为了获取更好的运行速度，虚拟机（甚至是硬件系统本身的优化措施）可能会让内存优先存储于寄存器和高速缓存中，因为程序运行时主要访问读写的是工作内存。

而主内存与工作内存之间具体的交互协议，即一个变量如何从主内存拷贝到工作内存、如何从工作内存同步回主内存之间的实现细节，Java 内存模型中定义了 8 种操作来完成。

而且还规定在执行上述 8 种基本操作时必须满足如下规则：

*   不允许 read 和 load、store 和 write 操作之一单独出现，即不允许一个变量从主内存读取了但工作内存不接受，或者从工作内存发起了回写了但主内存不接受的情况出现。

*   不允许一个线程丢弃它的最近的 assign 操作，即变量在工作内存中改变了之后必须把该变化同步回主内存。

*   不允许一个线程无原因地（没有发生任何 assign 操作）把数据从线程的工作内存同步回主内存中。

*   一个新的变量只能在主内存中 “诞生”，不允许在工作内存中直接使用一个未被初始化（load 或 assign）的变量，换句话说，就是对一个变量实施 use、store 操作之前，必须先执行过了 assign 和 load 操作。

*   一个变量在同一时刻只允许一条线程对其进行 lock 操作，但 lock 操作可以被同一条线程重复执行多次，多次执行 lock 后，只有执行相同次数的 unlock 操作，变量才会被解锁。

*   如果对一个变量执行 lock 操作，那将会清空工作内存中此变量的值，在执行引擎使用这个变量前，需要重新执行 load 或 assign 操作初始化变量的值。

*   如果一个变量事先没有被 lock 操作锁定, 那就不允许对它执行 unlock 操作, 也不允许去 unlock 一个被其他线程锁定住的变量。

*   对一个变量执行 unlock 操作之前，必须先把此变量同步回主内存中（执行 store、write 操作）。

以上 8 种内存访问操作以及上述规则限定，再加上 volatile 的一些特殊规定以及 final 不可变特性，就已经完成确定了 JAVA 程序中那些内存访问操作在并发下是安全的！

**JVM 参数总结**

为了方便大家对于 JVM 有关参数有一个参照，如下：

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVutokVL8r9J3zTicCRNSC2EXwW2EELicg4DqmA9SDvusia14ooiaJA3nnbhw8k29eic7THnFouPj0TPcU92iag/640?wx_fmt=jpeg)

**后记**

读到这里，小编希望能够对大家温习基础知识起到一定的帮助，特别是从事 Java 开发工作时间并不长的朋友希望本文能对你们有所促进，因为根据作者的经验，有时候很多从事 Java 开发工作好几年的同学，都会对这些知识点产生模糊的认识，一方面是目前各类 Java 开源工具比较完备，另一方面是很多人从事的是业务研发工作，时间久了难免会对基础知识有所遗忘。在上面的部分中还有一块垃圾回收的知识点没有总结到，基于篇幅的原因后面再单独给大家总结！谢谢你们的关注~

<section style="margin: 10px 0% 30px;opacity: 0.8;box-sizing: border-box;">

<section>

<section class="">

<section>

<section>

<section class="" data-style-type="5" data-tools="新媒体排版" data-id="979392">

—————END—————

如果觉得小哥在认真写文章，可以关注下公众号，支持下哦![](https://res.wx.qq.com/mpres/htmledition/images/icon/common/emotion_panel/smiley/smiley_0.png)

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVuton5KOpE65Uoh7kf3PZ9yRbtyBMAuSscgOFicWLHWWO8KBIibf8WeI7lqvrbr9SOs8wnVteDUwLg2YicA/640?wx_fmt=jpeg)

</section>

</section>

</section>

</section>

</section>

</section>