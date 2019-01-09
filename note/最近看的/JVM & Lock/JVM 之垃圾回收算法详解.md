> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483998&idx=1&sn=5ae39fc0587c954378c4c389ad8bbe8a&chksm=fd2fd39cca585a8a3b3aafee498a2cd1ddad80f61745ca29f0ec79be7489d45516ddbb4e27b0&scene=21#wechat_redirect

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutolu35tGfEL6Noe6QgyicDnsPuicSjm60hvn5B1bEvqmNic8eD3FhbqcicGk17Psia6h6YGpCaS8g7kWicag/640?wx_fmt=png)

**导读**

在之前的内容中，我们通过一张图的方式（图👆），从总体上对 JVM 的结构特别是内存结构有了比较清晰的认识，虽然在 JDK1.8 + 的版本中，JVM 内存管理结构有了一定的优化调整。主要是方法区（持久代）取消变成了直接使用**元数据区（直接内存）**的方式，但是整体上 JVM 的结构并没有大改，特别是我们最为关心的堆内存管理方式并没有在 JDK1.8 + 的版本中有什么变化，所以图中的结构整体上是没有什么不准确的，之所以将方法区以及持久代标注出来，主要还是为了起到对比认识的作用，大家知道就可以了。

关于持久代元数据区的使用问题，目前可以理解就是使用的物理内存，理论上是不受 JVM 自动内存回收机制管理的，如果不设置参数大小默认最大使用限制就是操作系统可用物理内存的大小，设置了 **-XX:MetaspaceSize** 参数的话，JVM 就会在使用物理内存空间时自己进行限制。

至于**直接内存与物理内存到底**是不是一回事，我认为对于我们理解上没有区别，只是概念的区别，另外就是对这块内存使用细节上的区别，如果不受 JVM 的自动回收管理，那么怎么管理呢？说到底还是 JVM 本身在直接使用物理内存或者说是直接内存（用时直接 **“malloc”** 物理内存区域，而不再是 JVM 进程启动时初始化的内存区域），还有一种概念叫 native memory，说实话我暂时还不理解他们到底有啥区别，如果大家对这些概念有更好的认识，也可以给我留言哦！之所以对这几个问题做一些笔墨的说明，主要是在之前的文章中大家对此提出了疑问，所以正好在这节的内容中进行下阐述。

回到今天的主题，我们知道 JAVA 最大的优点就是可以实现**自动内存管理**，这极大的便利了 JAVA 程序员，降低了使用成本。但这也使得平时我们在使用 JAVA 编程时不太关注 JVM 到底是怎样进行内存回收的，只有在需要实际对 JVM 进行系统性能调优，这里的场景可能是在系统面临极致性能优化要求时，我们才发现需要对 JAVA 的整体内存结构以及内存回收机制要有一定的认识和了解才行。

在👆的图中，我们也大致对整个垃圾回收系统进行了标注，这里主要涉及**回收策略**、**回收算法、垃圾回收器**这几个部分。形象一点表述，就是 JVM 需要知道那些内存可以被回收，要有一套识别机制，在知道那些内存可以回收以后具体采用什么样的回收方式，这就需要设计一些回收算法，而具体的垃圾回收器就是根据不同内存区域的使用特点，采用相应地回收策略和算法的具体实现了。

在👆图中，我们也标注了**不同垃圾回收器所适用的特定内存区域**，对于 JVM 垃圾回收这块的优化，就是我们需要在了解这些垃圾回收算法、垃圾回收器特点后能够根据自己应用的场景选择合适的垃圾收集器，以及各区域垃圾收集器的搭配关系。下面我们就从这几个方面给大家介绍，JVM 的垃圾回收相关的知识点。

**回收策略**

我们知道，JVM 进行内存回收的主要目的是为了回收**不再使用的内存**，因为在进行 JAVA 程序编写时，我们只有 **new** 的操作, 而不需要收工释放不再使用的空间，如果这些空闲内存不能及时被回收，很快我们的 JVM 内存空间就会**泄露**（新申请内存空间的操作失败，导致程序报错），所以回收不再使用的内存的目的则是为了**及时释放空间，腾笼换鸟，以防止内存泄漏**。

那么问题来了，JAVA 程序申请了那么多的内存空间，**那些内存才能被认定是不再使用的内存呢？**搞错了，如果把正在被程序使用的内存给释放了，程序逻辑就空指针异常了！

我们知道在 JVM 中内存分配的基本粒度主要是**对象、基本类型**。而基本类型的使用主要是包括在对象中的局部变量，所以回收对象所占用的内存是 JAVA 垃圾回收的主要目标。

那么如何判断对象是处于可回收状态的呢？在主流的 JVM 中是采用 “**可达性分析算法**” 来进行判断的。

这个算法的基本思路就是通过一系列的称为 “**GC Roots**” 的对象作为起始点，并从这些节点开始往下进行搜索，搜索走过的路径我们称之为**引用链（Reference Chain）**，当一个对象到 GC Roots 没有任何引用链相连时，我们就称之为**对象引用不可达**，则证明这个对象是不可用的，就可以暂时判定这个对象为可回收对象。示意图如下：

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvaEGMqXQ25ibwYQltOVsSvbEGHYXDv8bbXqC3CdAGgUDEfFu4ns1J52A/640?wx_fmt=png)

在图中虽然 Obj F 与 Obj J 之间互相有关联但是它们到 GC Roots 是不可达的，所以将会被判定为可回收对象。既然如此，什么样的对象可以作为 GC Roots 对象呢？

在 JAVA 中可以被作为 GC Roots 的对象主要是：**虚拟机栈 - 栈帧中的本地变量表所引用的对象**、方法区（<JDK1.8）中类静态属性所引用的对象／常量属性所引用的对象、本地方法栈中引用的对象。

这里还需要注意一个小的细节，就是被判定为对象不可达的对象也并非会被立刻回收，在学习 JAVA 语法是我们应该学习过 finalize() 方法，如果对象重写了 finalize 方法，并重新把 this 关键字赋值给了某个类变量或对象的成员变量的话，该对象就会被 **"救活"，**具体过程可参考上图所示，只是这种方式并不鼓励大家使用，了解下就行。

在关于如何判定对象是否属于不再使用的内存时，还有个通常会被大家错误认为是 JVM 使用的方式 -**“引用计数法”，**事实上引用计数法的实现比较简单，判定效率也比较高，在 Python 语言中就使用了这种算法进行内存管理，但是它有一个比较难解决的**对象之间循环引用**的问题，所以在 **JAVA 虚拟机里并没有选用 “引用计数法” 来管理内存**。这个问题很多人都会搞错，包括有很多年开发经验的程序员，需要大家注意下！

**回收算法**

在 JVM 中主要的垃圾收集算法有：**标记 - 清除**、标记 - 清除 - 压缩（简称 **“标记 - 整理”**）、标记 - 复制 - 清除（简称 **“复制”**）**、分代收集算法**。这几种收集算法互相配合，针对不同的内存区域采取对应的收集算法实现（这里具体是由相应的垃圾收集器实现）**。**

下面我们就分别来看下这几种收集算法的特点：

**1）、标记 - 清除** 

标记 - 清除算法是最为基础的一种收集算法，算法分为：“标记”和 “清除” 两个阶段。首先标记出所有需要回收的对象（标记的过程就是上面介绍过的根节点可达算法），在标记完后统一回收所有被标记对象占用的内存空间。

示意图如下：

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvOjsrr58G1NibJhUb6DvsOmEnA7mQ6fzlDsRibTm54icclu6pt3gAiauojg/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvXtVbrPyqfiaDlhPKYRv1AE622hw8DpGp6OjWcfS4ot1nPgicoH31WZeQ/640?wx_fmt=jpeg)

这种收集算法的优点是简单直接，不会影响 JVM 进程的正常运行。而其缺点也是非常明显，首先，这样的回收方式会产生大量不连续的**内存碎片**，不利于后续连续内存的分配；其次，这种方式的效率也不高。

**2）、标记 - 复制 - 清除** 

这种算法的思路是将可用的内存空间按容量划分为大小相等的两块，每次只使用其中一块。当这一块使用完了，就将还存活着的对象复制到另外一块上面（移动堆顶指针，按顺序分配内存），然后再把已使用过的内存空间一次清理掉。

示意图如下：

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvyo95reMUq3XO0Mj9z6ZS2jfvcOEUCVWLvwarIPtE9EqLz0xx5SkQMA/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvLUROknSUZmRYAQqLv4WV5QsibPmVibibsEPCLA7KEn52tV68rBdhDguNg/640?wx_fmt=png)

这种收集方式比较好的解决了效率和内存碎片的问题，但是会浪费掉一般的内存空间。目前此种算法主要用于**新生代回收**（文顶的图中有标注）。

因为新生代的中 98% 的对象都是很快就需要被回收的对象，这一点大家在编程时可以体会到，所以并不需要 1:1 的比例来划分内存空间，在新生代中 JVM 是按照 “**8:1:1**” 的比例（文顶图中有标注）来将整个新生代内存划分为一块**较大的 Eden 区**和两块较小的 Survivor 区（S0、S1）。

每次使用 Eden 区和其中一个 Survivor 区，当发生回收时将 Eden 区和 Survivor 区中还**存活的对象一次性复制到另一块 Survivor 区上**，最后清理掉 Eden 区和刚才使用过的 Survivor 区。理想情况下，每次新生代中的可用空间是整个新生代容量的 90%（80%+10%），只会有 10% 的内存会被浪费。实际情况中，如果另外一个 10% 的 Survivor 区无法装下所有还存活的对象时，就会将这些对象直接放入**老年代**空间中（这块在后面的分代回收算法会说到，这里先了解下）。

**3）、标记 - 清除 - 压缩**

如果在对象存活率较高的情况下，仍然采用复制算法的话，因为要进行较多的复制操作，效率就会变得很低，而且如果不想浪费 50% 的内存空间的话，就还需要额外的空间进行**分配担保****，**以应对**存活对象超额**的情况**。**显然老年代不能采用 2）中的复制算法。

根据老年代的特点，标记 - 清除 - 压缩（简称标记 - 整理）算法应运而生，这种算法的**标记过程**仍然与 “标记 - 清除” 算法一样，只是后续的步骤不再是直接清除可以回收的对象，而是将所有存活的对象都向一端移动后，再直接清理掉端边界以外的内存。

示意图如下：

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvj9gjl4Av1jJhZOhuwAr4borjyuiaBrO9e6ibSjBCaJqic5G1CFWRNzaYg/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutokK2Ex4fwl74Uf3P6EjpXrvLuaQyMjDiaYibZpP7HyfDianJQH0ujIZaMyhAeMRh81zknZWWFwtwLgtA/640?wx_fmt=png)

**4）、分代回收算法**

实际上在讲解复制算法时已经涉及到了分代回收的内容，这种算法根据对象存活周期的不同将内存划分为几块，Java 中主要是新生代、年老代**。**这样就可以根据各个年代的特点，采用合适的收集算法了**，**在文顶的图中已经标示，新生代采用了复制算法，而老年代采用了整理算法**，**这里就不再赘述**。** 

**垃圾回收器**

关于垃圾回收器部分的内容，由于篇幅的关系会在后续的《一张图看懂 JVM 之垃圾回收器详解》一文中进行讲解，敬请关注！

**如果对往期文章感兴趣可以👇这里**

[一张图看懂 JVM（升级版）](http://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483820&idx=1&sn=8418f0f6a618bb0f0ca0980af09a816f&chksm=fd2fd06eca5859786ab124dd204a7ec9b1ad3ed230b9b531086cc6729a277a05d3e8307b7e0d&scene=21#wechat_redirect)

[](http://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483820&idx=1&sn=8418f0f6a618bb0f0ca0980af09a816f&chksm=fd2fd06eca5859786ab124dd204a7ec9b1ad3ed230b9b531086cc6729a277a05d3e8307b7e0d&scene=21#wechat_redirect)[一张图看懂 JVM](http://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483807&idx=1&sn=aa7a25fe8d4a731cfa0a3890cf100041&chksm=fd2fd05dca58594bd08fe37741fd86b5aea2c2a274f6853b3ba3f8492eaaa599a008586357bb&scene=21#wechat_redirect) 

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