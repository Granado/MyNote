> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/Shw0jtVse1QqNbFCyYmfZA

### 引子

最近遇到很多朋友过来咨询 G1 调优的问题，我自己去年有专门学过一次 G1，但是当时只是看了个皮毛，因此自己也有不少问题。总体来讲，对于 G1 我有几个疑惑，希望能够在这篇文章中得到解决。

1.  G1 出现的初衷是什么？

2.  G1 适合在什么场景下使用？

3.  G1 的 trade-off 是什么？

4.  G1 的详细过程？

5.  如何理解 G1 的 gc 日志?

6.  G1 的调优思路？

7.  G1 和 CMS 的对比和选择？

### 一、基础知识

#### 1\. 初衷

在 G1 提出之前，经典的垃圾收集器主要有三种类型：串行收集器、并行收集器和并发标记清除收集器，这三种收集器分别可以是满足 Java 应用三种不同的需求：内存占用及并发开销最小化、应用吞吐量最大化和应用 GC 暂停时间最小化，但是，上述三种垃圾收集器都有几个共同的问题：（1）所有针对老年代的操作必须扫描整个老年代空间；（2）新生代和老年代是独立的连续的内存块，必须先决定年轻代和老年代在虚拟地址空间的位置。

#### 2\. 设计目标

G1 是一种服务端应用使用的垃圾收集器，目标是用在**多核、大内存**的机器上，它在大多数情况下可以实现指定的 GC 暂停时间，同时还能保持较高的吞吐量。

#### 3\. 使用场景

G1 适用于以下几种应用：

*   可以像 CMS 收集器一样，允许垃圾收集线程和应用线程并行执行，即需要额外的 CPU 资源；

*   压缩空闲空间不会延长 GC 的暂停时间；

*   需要更易预测的 GC 暂停时间；

*   不需要实现很高的吞吐量

### 二、G1 的重要概念

#### 1\. 分区（Region）

G1 采取了不同的策略来解决并行、串行和 CMS 收集器的碎片、暂停时间不可控制等问题——G1 将整个堆分成相同大小的**分区（Region）**，如下图所示。

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibhaVs1exTrz3XMRZ53Ungicjer6gtsq1oao3ua28E4oVaLPBZ3licDYIw/640?wx_fmt=png)G1 的堆模型

每个分区都可能是年轻代也可能是老年代，但是在同一时刻只能属于某个代。
年轻代、幸存区、老年代这些概念还存在，成为逻辑上的概念，这样方便复用之前分代框架的逻辑。在物理上不需要连续，则带来了额外的好处——有的分区内垃圾对象特别多，有的分区内垃圾对象很少，G1 会优先回收垃圾对象特别多的分区，这样可以花费较少的时间来回收这些分区的垃圾，这也就是 G1 名字的由来，即首先收集垃圾最多的分区。

新生代其实并不是适用于这种算法的，依然是在新生代满了的时候，对整个新生代进行回收——整个新生代中的对象，要么被回收、要么晋升，至于新生代也采取分区机制的原因，则是因为这样跟老年代的策略统一，方便调整代的大小。

G1 还是一种带压缩的收集器，在回收老年代的分区时，是将存活的对象从一个分区拷贝到另一个可用分区，这个拷贝的过程就实现了局部的压缩。每个分区的大小从 1M 到 32M 不等，但是都是 2 的冥次方。

#### 2\. 收集集合（CSet）

一组可被回收的分区的集合。在 CSet 中存活的数据会在 GC 过程中被移动到另一个可用分区，CSet 中的分区可以来自 Eden 空间、survivor 空间、或者老年代。CSet 会占用不到整个堆空间的 1% 大小。

#### 3\. 已记忆集合（RSet）

RSet 记录了其他 Region 中的对象引用本 Region 中对象的关系，属于 points-into 结构（谁引用了我的对象）。RSet 的价值在于使得垃圾收集器不需要扫描整个堆找到谁引用了当前分区中的对象，只需要扫描 RSet 即可。

如下图所示，Region1 和 Region3 中的对象都引用了 Region2 中的对象，因此在 Region2 的 RSet 中记录了这两个引用。

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibHdl8Zfss8TNrDa5ic5wLLu8KOU4pV3xePh4mt6vAFib9QeY62Ora2szQ/640?wx_fmt=png)RSet 的示意图

> 摘一段 R 大的解释：G1 GC 则是在 points-out 的 card table 之上再加了一层结构来构成 points-into RSet：每个 region 会记录下到底哪些别的 region 有指向自己的指针，而这些指针分别在哪些 card 的范围内。 这个 RSet 其实是一个 hash table，key 是别的 region 的起始地址，value 是一个集合，里面的元素是 card table 的 index。 举例来说，如果 region A 的 RSet 里有一项的 key 是 region B，value 里有 index 为 1234 的 card，它的意思就是 region B 的一个 card 里有引用指向 region A。所以对 region A 来说，该 RSet 记录的是 points-into 的关系；而 card table 仍然记录了 points-out 的关系。

#### 4\. Snapshot-At-The-Beginning(SATB)

SATB 是维持并发 GC 的正确性的一个手段，G1GC 的并发理论基础就是 SATB，SATB 是由 Taiichi Yuasa 为增量式标记清除垃圾收集器设计的一个标记算法。Yuasa 的 SATAB 的标记优化主要针对标记 - 清除垃圾收集器的并发标记阶段。按照 R 大的说法：CMS 的 incremental update 设计使得它在 remark 阶段必须重新扫描所有线程栈和整个 young gen 作为 root；G1 的 SATB 设计在 remark 阶段则只需要扫描剩下的 satb_mark_queue。

SATB 算法创建了一个对象图，它是堆的一个逻辑 “快照”。标记数据结构包括了两个位图：previous 位图和 next 位图。previous 位图保存了最近一次完成的标记信息，并发标记周期会创建并更新 next 位图，随着时间的推移，previous 位图会越来越过时，最终在并发标记周期结束的时候，next 位图会将 previous 位图覆盖掉。
下面我们以几个图例来描述 SATB 算法的过程：

在并发周期开始之前，NTAMS 字段被设置到每个分区当前的顶部，并发周期启动后分配的对象会被放在 TAMS 之前（图里下边的部分），同时被明确定义为隐式存活对象，而 TAMS 之后（图里上边的部分）的对象则需要被明确地标记。

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibtic2An1dfYdClgYvQFFknNPfF6YvY7V7rJAEsSXfLZkJ4dpxicbQgxbQ/640?wx_fmt=png)初始标记过程中的一个堆分区

并发标记过程中的堆分区

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibOrqHjA6XNibibiaKnyuamsgutnnuY7Wn32JKPKrOzIHmia4R2bzk8vthZQ/640?wx_fmt=png)并发标记过程中的对分区

位于堆分区的 Bottom 和 PTAMS 之间的对象都会被标记并记录在 previous 位图中；

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibiazds42oe12ibK4kOL1icXNGRt6cr3XbS0crEOBeYngicgHKkRhsshQa1A/640?wx_fmt=png)位于 Bottom 和 PTAMS 之间的对象都会被标记在 previous 位图中

位于堆分区的 Top 和 PATMS 之间的对象均为隐式存活对象，同时也记录在 previous 位图中；

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibB10IsPbk585iaiaL1bKN4b9kpOMibye4PE87Pf5ibicreInoYlsK5POWUCQ/640?wx_fmt=png)隐式存活标记，是一种增量标记

在重新标记阶段的最后，所有 NTAMS 之前的对象都会被标记

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibAsLxHkZk7EamWziaSZzghp4ib9SOQjQUW5FhAoCgGkV4ZrCmTkMYdA7Q/640?wx_fmt=png)重新标记

在并发标记阶段分配的对象会被分配到 NTAMS 之后的空间，它们会作为隐式存活对象被记录在 next 位图中。一次并发标记周期完成后，这个 next 位图会覆盖 previous 位图，然后将 next 位图清空。

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibTDXMn5Ls9WY50btPCicNKuKOUCMHbC7NdGwyGoOXDL3dR6WjHicU5HvA/640?wx_fmt=png)开始并发标记后的对象会被识别为隐式存活对象，放在 next 位图中

SATB 是一个快照标记算法，在并发标记进行的过程中，垃圾收集器（Collecotr）和应用程序（Mutator）都在活动，如果一个对象还没被 mark 到，这时候 Mutator 就修改了它的引用，那么这时候拿到的快照就是不完整的了，如何解决这个问题呢?

G1 GC 使用了 SATB write barrier 来解决这个问题——在并发标记过程中，将该对象的旧的引用记录在一个 SATB 日志对列或缓冲区中。去翻 G1 的代码，却发现实际代码如下——只该对象入队列，并没有将整个修改过程放在写屏障之间完成。

```
  // hotspot/src/share/vm/gc_implementation/g1/g1SATBCardTableModRefBS.hpp  // This notes that we don't need to access any BarrierSet data  // structures, so this can be called from a static context.  template <class T> static void write_ref_field_pre_static(T* field, oop newVal) {    T heap_oop = oopDesc::load_heap_oop(field);    if (!oopDesc::is_null(heap_oop)) {      enqueue(oopDesc::decode_heap_oop(heap_oop));    }  }
```

enqueue 的真正代码在`hotspot/src/share/vm/gc_implementation/g1/g1SATBCardTableModRefBS.cpp`中，这里使用`JavaThread::satb_mark_queue_set().is_active()`判断是否处于并发标记周期。

```
void G1SATBCardTableModRefBS::enqueue(oop pre_val) {  // Nulls should have been already filtered.  assert(pre_val->is_oop(true), "Error");  if (!JavaThread::satb_mark_queue_set().is_active()) return;  Thread* thr = Thread::current();  if (thr->is_Java_thread()) {    JavaThread* jt = (JavaThread*)thr;    //将旧值入队    jt->satb_mark_queue().enqueue(pre_val);  } else {    MutexLockerEx x(Shared_SATB_Q_lock, Mutex::_no_safepoint_check_flag);    JavaThread::satb_mark_queue_set().shared_satb_queue()->enqueue(pre_val);  }}
```

stab_mark_queue.enqueue 方法首先尝试将以前的值记录在一个缓冲区中，如果这个缓冲区已经满了，就会将当期这个 SATB 缓冲区 “退休” 并放入全局列表中，然后再给线程分配一个新的 SATB 缓冲区。并发标记线程会定期检查和处理那些 “被填满” 的缓冲区。

### 三、G1 的过程

#### 1\. 四个操作

G1 收集器的收集活动主要有四种操作：

*   新生代垃圾收集

*   后台收集、并发周期

*   混合式垃圾收集

*   必要时候的 Full GC

第一、新生代垃圾收集的图例如下：

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibumARrl2fUAMdqWhBoic1356UKic6Gz3MiaXSJsHJNxSMMYTmv7vRXKs6Q/640?wx_fmt=png)image.png

*   Eden 区耗尽的时候就会触发新生代收集，新生代垃圾收集会对整个新生代进行回收

*   新生代垃圾收集期间，整个应用 STW

*   新生代垃圾收集是由多线程并发执行的

*   新生代收集结束后依然存活的对象，会被拷贝到一个新的 Survivor 分区，或者是老年代。

G1 设计了一个标记阈值，它描述的是总体 Java 堆大小的百分比，默认值是 45，这个值可以通过命令`-XX:InitiatingHeapOccupancyPercent(IHOP)`来调整，一旦达到这个阈值就回触发一次并发收集周期。注意：这里的百分比是针对整个堆大小的百分比，而 CMS 中的`CMSInitiatingOccupancyFraction`命令选型是针对老年代的百分比。并发收集周期的图例如下：

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibqHb1FKGDgEiam1jibLjjMgicm3wAOmokpRMa9J8lK7gBgWMY3FEKJAcvQ/640?wx_fmt=png)image.png

在上图中有几个情况需要注意：

1、新生代的空间占用情况发生了变化——在并发收集周期中，至少有一次（很可能是多次）新生代垃圾收集；

2、注意到一些分区被标记为 X，这些分区属于老年代，它们就是标记周期找出的包含最多垃圾的分区（注意：它们内部仍然保留着数据）；

3、老年代的空间占用在标记周期结束后变得更多，这是因为在标记周期期间，新生代的垃圾收集会晋升对象到老年代，而且标记周期中并不会是否老年代的任何对象。

第二、G1 的并发标记周期包括多个阶段：
并发标记周期采用的算法是我们前文提到的 SATB 标记算法，产出是找出一些垃圾对象最多的老年代分区。

1、初始标记（initial-mark），在这个阶段，应用会经历 STW，通常初始标记阶段会跟一次新生代收集一起进行，换句话说——既然这两个阶段都需要暂停应用，G1 GC 就重用了新生代收集来完成初始标记的工作。

在新生代垃圾收集中进行初始标记的工作，会让停顿时间稍微长一点，并且会增加 CPU 的开销。初始标记做的工作是设置两个 TAMS 变量（NTAMS 和 PTAMS）的值，所有在 TAMS 之上的对象在这个并发周期内会被识别为隐式存活对象；

2、根分区扫描（root-region-scan），这个过程不需要暂停应用，在初始标记或新生代收集中被拷贝到 survivor 分区的对象，都需要被看做是根，这个阶段 G1 开始扫描 survivor 分区，所有被 survivor 分区所引用的对象都会被扫描到并将被标记。

survivor 分区就是根分区，正因为这个，该阶段不能发生新生代收集，如果扫描根分区时，新生代的空间恰好用尽，新生代垃圾收集必须等待根分区扫描结束才能完成。如果在日志中发现根分区扫描和新生代收集的日志交替出现，就说明当前应用需要调优。

3、并发标记阶段（concurrent-mark），并发标记阶段是多线程的，我们可以通过`-XX:ConcGCThreads`来设置并发线程数，默认情况下，G1 垃圾收集器会将这个线程总数设置为并行垃圾线程数（`-XX:ParallelGCThreads`）的四分之一；并发标记会利用 trace 算法找到所有活着的对象，并记录在一个 bitmap 中，因为在 TAMS 之上的对象都被视为隐式存活，因此我们只需要遍历那些在 TAMS 之下的；

记录在标记的时候发生的引用改变，SATB 的思路是在开始的时候设置一个快照，然后假定这个快照不改变，根据这个快照去进行 trace，这时候如果某个对象的引用发生变化，就需要通过 pre-write barrier logs 将该对象的旧的值记录在一个 SATB 缓冲区中，如果这个缓冲区满了，就把它加到一个全局的列表中——G1 会有并发标记的线程定期去处理这个全局列表。

4、重新标记阶段（remarking），重新标记阶段是最后一个标记阶段，需要暂停整个应用，G1 垃圾收集器会处理掉剩下的 SATB 日志缓冲区和所有更新的引用，同时 G1 垃圾收集器还会找出所有未被标记的存活对象。这个阶段还会负责引用处理等工作。

5、清理阶段（cleanup），清理阶段真正回收的内存很小，截止到这个阶段, G1 垃圾收集器主要是标记处哪些老年代分区可以回收，将老年代按照它们的存活度（liveness）从小到大排列。

这个过程还会做几个事情：识别出所有空闲的分区、RSet 梳理、将不用的类从 metaspace 中卸载、回收巨型对象等等。识别出每个分区里存活的对象有个好处是在遇到一个完全空闲的分区时，它的 RSet 可以立即被清理，同时这个分区可以立刻被回收并释放到空闲队列中，而不需要再放入 CSet 等待混合收集阶段回收；梳理 RSet 有助于发现无用的引用。

第三、混合收集只会回收一部分老年代分区，下图是第一次混合收集前后的堆情况对比。

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibGia9nZMjM8NGRRAXfU4RGQhUjufx5MJEPdnD0r2G33YorDZbuyaswaQ/640?wx_fmt=png)image.png

混合收集会执行多次，一直运行到（几乎）所有标记点老年代分区都被回收，在这之后就会恢复到常规的新生代垃圾收集周期。当整个堆的使用率超过指定的百分比时，G1 GC 会启动新一轮的并发标记周期。在混合收集周期中，对于要回收的分区，会将该分区中存活的数据拷贝到另一个分区，这也是为什么 G1 收集器最终出现碎片化的频率比 CMS 收集器小得多的原因——以这种方式回收对象，实际上伴随着针对当前分区的压缩。

#### 2\. 两个模式

G1 收集器的模式主要有两种：

*   Young GC（新生代垃圾收集）

*   Mixed GC（混合垃圾收集）

在 R 大的帖子中，给出了一个假象的 G1 垃圾收集运行过程，如下图所示，在结合上一小节的细节，就可以将 G1 GC 的正常过程理解清楚了。

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibYfMUsZ3YTTticwEycLAqU1E52icm9JGR6sNcribzHnoE09ISkyPvmBZcw/640?wx_fmt=png)image.png

#### 3\. 巨型对象的管理

**巨型对象**：在 G1 中，如果一个对象的大小超过分区大小的一半，该对象就被定义为**巨型对象（Humongous Object）**。巨型对象时直接分配到老年代分区，如果一个对象的大小超过一个分区的大小，那么会直接在老年代分配两个连续的分区来存放该巨型对象。巨型分区一定是连续的，分配之后也不会被移动——没啥益处。

由于巨型对象的存在，G1 的堆中的分区就分成了三种类型：新生代分区、老年代分区和巨型分区，如下图所示：

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibqWI8RzshRWOodmrSfPcOFcSPRvGbvhMHYFflpuzCk7iaYh9v0kTAdkg/640?wx_fmt=png)image.png

如果一个巨型对象跨越两个分区，开始的那个分区被称为 “开始巨型”，后面的分区被称为 “连续巨型”，这样最后一个分区的一部分空间是被浪费掉的，如果有很多巨型对象都刚好比分区大小多一点，就会造成很多空间的浪费，从而导致堆的碎片化。如果你发现有很多由于巨型对象分配引起的连续的并发周期，并且堆已经碎片化（明明空间够，但是触发了 FULL GC），可以考虑调整`-XX:G1HeapRegionSize`参数，减少或消除巨型对象的分配。

关于巨型对象的回收：在 JDK8u40 之前，巨型对象的回收只能在并发收集周期的清除阶段或 FULL GC 过程中过程中被回收，在 JDK8u40（包括这个版本）之后，一旦没有任何其他对象引用巨型对象，那么巨型对象也可以在年轻代收集中被回收。

#### 4\. G1 执行过程中的异常情况

##### 并发标记周期开始后的 FULL GC

G1 启动了标记周期，但是在并发标记完成之前，就发生了 Full GC，日志常常如下所示：

```
51.408: [GC concurrent-mark-start]65.473: [Full GC 4095M->1395M(4096M), 6.1963770 secs] [Times: user=7.87 sys=0.00, real=6.20 secs]71.669: [GC concurrent-mark-abort]
```

GC concurrent-mark-start 开始之后就发生了 FULL GC，这说明针对老年代分区的回收速度比较慢，或者说对象过快得从新生代晋升到老年代，或者说是有很多大对象直接在老年代分配。针对上述原因，我们可能需要做的调整有：调大整个堆的大小、更快得触发并发回收周期、让更多的回收线程参与到垃圾收集的动作中。

##### 混合收集模式中的 FULL GC

在 GC 日志中观察到，在一次混合收集之后跟着一条 FULL GC，这意味着混合收集的速度太慢，在老年代释放出足够多的分区之前，应用程序就来请求比当前剩余可分配空间大的内存。针对这种情况我们可以做的调整：增加每次混合收集收集掉的老年代分区个数；增加并发标记的线程数；提高混合收集发生的频率。

##### 疏散失败（转移失败）

在新生代垃圾收集快结束时，找不到可用的分区接收存活下来的对象，常见如下的日志：

```
60.238: [GC pause (young) (to-space overflow), 0.41546900 secs]
```

这意味着整个堆的碎片化已经非常严重了，我们可以从以下几个方面调整：（1）增加整个堆的大小——通过增加`-XX:G1ReservePercent`选项的值（并相应增加总的堆大小），为 “目标空间” 增加预留内存量;（2）通过减少 `-XX:InitiatingHeapOccupancyPercent`提前启动标记周期；（3）
你也可以通过增加`-XX:ConcGCThreads`选项的值来增加并发标记线程的数目；

##### 巨型对象分配失败

如果在 GC 日志中看到莫名其妙的 FULL GC 日志，又对应不到上述讲过的几种情况，那么就可以怀疑是巨型对象分配导致的，这里我们可以考虑使用`jmap`命令进行堆 dump，然后通过 MAT 对堆转储文件进行分析。关于堆转储文件的分析技巧，后续会有专门的文章介绍。

### 四、G1 的调优

G1 的调优目标主要是在避免 FULL GC 和疏散失败的前提下，尽量实现较短的停顿时间和较高的吞吐量。关于 G1 GC 的调优，需要记住以下几点：

1、不要自己显式设置新生代的大小（用`Xmn`或`-XX:NewRatio`参数），如果显式设置新生代的大小，会导致目标时间这个参数失效。

2、由于 G1 收集器自身已经有一套预测和调整机制了，因此我们首先的选择是相信它，即调整`-XX:MaxGCPauseMillis=N`参数，这也符合 G1 的目的——让 GC 调优尽量简单，这里有个取舍：如果减小这个参数的值，就意味着会调小新生代的大小，也会导致新生代 GC 发生得更频繁，同时，还会导致混合收集周期中回收的老年代分区减少，从而增加 FULL GC 的风险。这个时间设置得越短，应用的吞吐量也会受到影响。

3、针对混合垃圾收集的调优。如果调整这期望的最大暂停时间这个参数还是无法解决问题，即在日志中仍然可以看到 FULL GC 的现象，那么就需要自己手动做一些调整，可以做的调整包括：

1）调整 G1 垃圾收集的后台线程数，通过设置`-XX:ConcGCThreads=n`这个参数，可以增加后台标记线程的数量，帮 G1 赢得这场你追我赶的游戏；

2）调整 G1 垃圾收集器并发周期的频率，如果让 G1 更早得启动垃圾收集，也可以帮助 G1 赢得这场比赛，那么可以通过设置`-XX:InitiatingHeapOccupancyPercent`这个参数来实现这个目标，如果将这个参数调小，G1 就会更早得触发并发垃圾收集周期。

这个值需要谨慎设置：如果这个参数设置得太高，会导致 FULL GC 出现得频繁；如果这个值设置得过小，又会导致 G1 频繁得进行并发收集，白白浪费 CPU 资源。通过 GC 日志可以通过一个点来判断 GC 是否正常——在一轮并发周期结束后，需要确保堆剩下的空间小于 InitiatingHeapOccupancyPercent 的值。

3）调整 G1 垃圾收集器的混合收集的工作量，即在一次混合垃圾收集中尽量多处理一些分区，可以从另外一方面提高混合垃圾收集的频率。在一次混合收集中可以回收多少分区，取决于三个因素：

（1）有多少个分区被认定为垃圾分区，`-XX:G1MixedGCLiveThresholdPercent=n`这个参数表示如果一个分区中的存活对象比例超过 n，就不会被挑选为垃圾分区，因此可以通过这个参数控制每次混合收集的分区个数，这个参数的值越大，某个分区越容易被当做是垃圾分区；

（2）G1 在一个并发周期中，最多经历几次混合收集周期，这个可以通过`-XX:G1MixedGCCountTarget=n`设置，默认是 8，如果减小这个值，可以增加每次混合收集收集的分区数，但是可能会导致停顿时间过长；

（3）期望的 GC 停顿的最大值，由`MaxGCPauseMillis`参数确定，默认值是 200ms，在混合收集周期内的停顿时间是向上规整的，如果实际运行时间比这个参数小，那么 G1 就能收集更多的分区。

### 五、G1 的最佳实践

#### 1\. 关键参数项

*   `-XX:+UseG1GC`，告诉 JVM 使用 G1 垃圾收集器

*   `-XX:MaxGCPauseMillis=200`，设置 GC 暂停时间的目标最大值，这是个柔性的目标，JVM 会尽力达到这个目标

*   `-XX:INitiatingHeapOccupancyPercent=45`，如果整个堆的使用率超过这个值，G1 会触发一次并发周期。记住这里针对的是整个堆空间的比例，而不是某个分代的比例。

#### 2\. 最佳实践

##### 不要设置年轻代的大小

通过`-Xmn`显式设置年轻代的大小，会干扰 G1 收集器的默认行为：

*   G1 不再以设定的暂停时间为目标，换句话说，如果设置了年轻代的大小，就无法实现自适应的调整来达到指定的暂停时间这个目标

*   G1 不能按需扩大或缩小年轻代的大小

##### 响应时间度量

不要根据平均响应时间（ART）来设置`-XX:MaxGCPauseMillis=n`这个参数，应该设置希望 90% 的 GC 都可以达到的暂停时间。这意味着 90% 的用户请求不会超过这个响应时间，记住，这个值是一个目标，但是 G1 并不保证 100% 的 GC 暂停时间都可以达到这个目标

#### 3\. G1 GC 的参数选项

| 参数名 | 含义 | 默认值 |
| --- | --- | --- |
| -XX:+UseG1GC | 使用 G1 收集器 | JDK1.8 中还需要显式指定 |
| -XX:MaxGCPauseMillis=n | 设置一个期望的最大 GC 暂停时间，这是一个柔性的目标，JVM 会尽力去达到这个目标 | 200 |
| -XX:InitiatingHeapOccupancyPercent=n | 当整个堆的空间使用百分比超过这个值时，就会触发一次并发收集周期，记住是整个堆 | 45 |
| -XX:NewRatio=n | 新生代和老年代的比例 | 2 |
| -XX:SurvivorRatio=n | Eden 空间和 Survivor 空间的比例 | 8 |
| -XX:MaxTenuringThreshold=n | 对象在新生代中经历的最多的新生代收集，或者说最大的岁数 | G1 中是 15 |
| -XX:ParallelGCThreads=n | 设置垃圾收集器的并行阶段的垃圾收集线程数 | 不同的平台有不同的值 |
| -XX:ConcGCThreads=n | 设置垃圾收集器并发执行 GC 的线程数 | n 一般是 ParallelGCThreads 的四分之一 |
| -XX:G1ReservePercent=n | 设置作为空闲空间的预留内存百分比，以降低目标空间溢出（疏散失败）的风险。默认值是 10%。增加或减少这个值，请确保对总的 Java 堆调整相同的量 | 10 |
| -XX:G1HeapRegionSize=n | 分区的大小 | 堆内存大小的 1/2000，单位是 MB，值是 2 的幂，范围是 1MB 到 32MB 之间 |
| -XX:G1HeapWastePercent=n | 设置您愿意浪费的堆百分比。如果可回收百分比小于堆废物百分比，JavaHotSpotVM 不会启动混合垃圾回收周期（注意，这个参数可以用于调整混合收集的频率）。 | JDK1.8 是 5 |
| -XX:G1MixedGCCountTarget=8 | 设置并发周期后需要执行多少次混合收集，如果混合收集中 STW 的时间过长，可以考虑增大这个参数。（注意：这个可以用来调整每次混合收集中回收掉老年代分区的多少，即调节混合收集的停顿时间） | 8 |
| -XX:G1MixedGCLiveThresholdPercent=n | 一个分区是否会被放入 mix GC 的 CSet 的阈值。对于一个分区来说，它的存活对象率如果超过这个比例，则改分区不会被列入 mixed gc 的 CSet 中 | JDK1.6 和 1.7 是 65，JDK1.8 是 85 |

### 常见问题

1、Young GC、Mixed GC 和 Full GC 的区别？
答：Young GC 的 CSet 中只包括年轻代的分区，Mixed GC 的 CSet 中除了包括年轻代分区，还包括老年代分区；Full GC 会暂停整个引用，同时对新生代和老年代进行收集和压缩。

2、ParallelGCThreads 和 ConcGCThreads 的区别？
答：ParallelGCThreads 指得是在 STW 阶段，并行执行垃圾收集动作的线程数，ParallelGCThreads 的值一般等于逻辑 CPU 核数，如果 CPU 核数大于 8，则设置为`5/8 * cpus`，在 SPARC 等大型机上这个系数是 5/16。；ConcGCThreads 指的是在并发标记阶段，并发执行标记的线程数，一般设置为 ParallelGCThreads 的四分之一。

3、write barrier 在 GC 中的作用？如何理解 G1 GC 中 write barrier 的作用？
写屏障是一种内存管理机制，用在这样的场景——当代码尝试修改一个对象的引用时，在前面放上写屏障就意味着将这个对象放在了写屏障后面。

write barrier 在 GC 中的作用有点复杂，我们这里以 trace GC 算法为例讲下：trace GC 有些算法是并发的，例如 CMS 和 G1，即用户线程和垃圾收集线程可以同时运行，即 mutator 一边跑，collector 一边收集。这里有一个限制是：黑色的对象不应该指向任何白色的对象。如果 mutator 视图让一个黑色的对象指向一个白色的对象，这个限制就会被打破，然后 GC 就会失败。

针对这个问题有两种解决思路：

（1）通过添加 read barriers 阻止 mutator 看到白色的对象；

（2）通过 write barrier 阻止 mutator 修改一个黑色的对象，让它指向一个白色的对象。write barrier 的解决方法就是讲黑色的对象放到写 write barrier 后面。如果真得发生了 white-on-black 这种写需求，一般也有多种修正方法：增量得将白色的对象变灰，将黑色的对象重新置灰等等。

我理解，增量的变灰就是 CMS 和 G1 里并发标记的过程，将黑色的对象重新变灰就是利用卡表或 SATB 的缓冲区将黑色的对象重新置灰的过程，当然会在重新标记中将所有灰色的对象处理掉。关于 G1 中 write barrier 的作用，可以参考 R 大的这个帖子里提到的：

![](https://mmbiz.qpic.cn/mmbiz_png/PgqYrEEtEnqRO95iaHqicavuChhib9o2WOibeRicMRWcpg9cvhvAH4kicxnggByic7WlvJpNcIsYMsE0bdibySFhiahianiaw/640?wx_fmt=png)image.png

4、G1 里在并发标记的时候，如果有对象的引用修改，要将旧的值写到一个缓冲区中，这个动作前后会有一个 write barrier，这段可否细说下？

答：这块涉及到 SATB 标记算法的原理，SATB 是指 start at the beginning，即在并发收集周期的第一个阶段（初始标记）是 STW 的，会给所有的分区做个快照，后面的扫描都是按照这个快照进行；在并发标记周期的第二个阶段，并发标记，这是收集线程和应用线程同时进行的，这时候应用线程就可能修改了某些引用的值，导致上面那个快照不是完整的，因此 G1 就想了个办法，我把在这个期间对对象引用的修改都记录动作都记录下来，有点像 mysql 的操作日志。

5、GC 算法中的三色标记算法怎么理解？
trace GC 将对象分为三类：白色（垃圾收集器未探测到的对象）、灰色（活着的对象，但是依然没有被垃圾收集器扫描过）、黑色（活着的对象，并且已经被垃圾收集器扫描过）。垃圾收集器的工作过程，就是通过灰色对象的指针扫描它指向的白色对象，如果找到一个白色对象，就将它设置为灰色，如果某个灰色对象的可达对象已经全部找完，就将它设置为黑色对象。当在当前集合中找不到灰色的对象时，就说明该集合的回收动作完成，然后所有白色的对象的都会被回收。

PS：这个问题来自参考资料 17，我将原文也贴在下面：

> For a tracing collector (marking or copying), one conceptually colours the data white (not yet seen by the collector), black (alive and scanned by the collector) and grey (alive but not yet scanned by the collector). The collector proceeds by scanning grey objects for pointers to white objects. The white objects found are turned grey, and the grey objects scanned are turned black. When there are no more grey objects, the collection is complete and all the white objects can be recycled.

### 参考资料

1.  Understanding G1 GC Logs

2.  Garbage First Garbage Collector Tuning

3.  垃圾优先型回收器调优

4.  Oracle 的 GC 调优文档——G1

5.  The Garbage-First Garbage Collector

6.  《Java 性能权威指南》

7.  《Java 性能调优指南》

8.  G1 入门，O 记官网的 PPT

9.  Java Hotspot G1 GC 的一些关键技术

10.  G1 GC 的论文

11.  R 大关于 G1 GC 的帖子

12.  Tips for Tuning the Garbage First Garbage Collector

13.  Java 性能调优指南

14.  Java 性能权威指南

15.  G1: What are the differences between mixed gc and full gc?

16.  Part 1: Introduction to the G1 Garbage Collector

17.  Collecting and reading G1 garbage collector logs - part 2

18.  GC FAQ -- algorithms

-END-

**一个有故事的程序员**

![](https://mmbiz.qpic.cn/mmbiz_jpg/PgqYrEEtEnqjV7GOKB2htgfZjgMjqxftxfmmdrLiaMKpyicTmLLX5fUjb6YxA6Z5Bhcozb3p0uMV7wqdKED89HZA/640?wx_fmt=jpeg)

觉得 “好看” 点击一下！