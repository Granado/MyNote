> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/ejwzL3m9yS10e8DPL4D3QQ

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutomVswbVF9ffNxfu6EukVdhCRLZvECvVTe45ibMA805oQblerVgF8UnEw42uRLLyFWGveUOg3Bh2qpA/640?wx_fmt=png)

**导读**

> 在之前的文章中，我们通过一张图的方式（图👆）整体上了解了 JVM 的结构，并重点讲解了 JVM 的内存结构、内存回收算法及回收器方面的知识。收到了不少读者朋友们的反馈和指正，在这里作者向这些提出中肯建议的读者朋友们表示感谢，谢谢你们的支持。
> 
> 在今天的文章中将主要和大家一起探讨关于类装载子系统的内容。我们知道，Java 源代码（.java 文件）需要通过编译器编译成字节码文件（.class）后由类装载子系统（ClassLoader）载入运行时数据区（<jdk1.8 之前是载入方法区，>=jdk1.8 以后是载入元数据区）才能被后续的 Java 运行程序（线程）正常使用（实例化或引用）。
> 
> 那么类装载的具体机制是什么样的呢？下面就让我们一起进一步来了解下吧！

**JVM 类装载概述**

与 C/C++ 那些需要在编译器期进行连接工作的语言不同，Java 类的加载、连接和初始化都是在程序运行时完成的，只有在类被需要的时候才进行动态加载，这种方式被称为 **“Java 语言的运行期类加载机制”**。

例如我们在实际使用 Java 语言进行编程时通常会编写一个面向接口的应用程序，可以等到运行时再指定其实际的实现类。此外，更高级一点的做法是可以通过**自定义的类加载器**（关于具体的 ClassLoader 在后面的内容会提到），让一个本地的应用程序可以在运行时从网络或其他地方加载一个二进制流作为程序代码的一部分（Applet 就是这么干的），这种组装应用程序的方式目前已广泛的应用于 Java 程序之中。

类（Class）从被加载到虚拟机内存中开始，到卸载出内存为止会经历如下生命周期：

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutonIa1gdaBtiaGviaoAEZYh9A96SREVfNJyHRJlyS7zI5TZtTUXk7T4twrib0LcEPyr4jeGHAMDpsxiaMw/640?wx_fmt=png)

其中验证、准备、解析 3 个部分又统称为连接（Linking）。在以上过程中，除解析外，加载、验证、准备、初始化、卸载这 5 个阶段的顺序都是确定的，JVM 规定类的加载过程必须按照这种顺序按部就班地开始。而解析阶段则不一定，为了支持 Java 语言的**运行时绑定**，解析过程在某些情况下可以在初始化阶段之后再开始。

需要注意的是，这些阶段并不是必须等到上一个阶段完成才能开始下一个阶段，这些阶段通常都是互相交叉地混合式进行的，会在一个阶段执行的过程中就会调用、激活另外一个阶段。

聊到这里，我们大概了解了从一个 class 字节码文件变成加载到内存中能够被使用的类，按照先后顺序需要经过加载、连接、初始化三大主要步骤。连接过程又需要经历验证、准备、解析三个阶段，完成后类被加载至内存，但此时并不能被使用，还需要经过初始化阶段。

那么，在 Java 中是否所有的类型在类加载的过程中都需要经过这几个步骤呢？

我们知道 Java 语言的类型可以分为两大类：**基本类型、引用类型**。基本类型是由虚拟机预先定义好的，所以不会经历单独的类加载过程。而引用类型又分为四种：**类、接口、数组类、泛型参数**。由于泛型参数会在编译的过程中被擦除（关于类型擦除的知识，大家可以查下资料），所以在 Java 中只有类、接口、数组类三种类型需要经历 JVM 对其进行连接和初始化的过程。

在上述三种类型中数组类是由 JVM 直接生成的，类和接口则有对应的字节流，字节流最常见的形式就是我们由编译器生成的 class 文件，另外的形式也有在前面说到的通过网络加载的二进制流（例如网页中内嵌的小程序 Java applet），这些不同形式的字节流都会被 JVM 加载到内存中，成为类或接口。

**JVM 类装载过程**

那么这些过程具体会干些什么事呢？接下来我们就详细了解下这些具体步骤的细节。

**|** **加载（Loading）**

“加载” 是 “类加载”（Class Loading）过程的一个阶段，是查找字节流并据此创建类的过程。在前面我们提到过说数组类因为没有对应的字节流所以是由 JVM 直接生成的，而对于类和接口来说则需要借助**类加载器**（后面会讲到）来完成查找字节流的过程。

但是我们在回答上面关于 Java 中哪些类型需要经历类加载的阶段时，又明确说了数组类型也是需要 JVM 对其进行连接和初始化的，这是不是有点矛盾呢？事实上，虽然数组类本身并不通过类加载器加载（由虚拟机直接创建），但是数组类与类加载器仍然有很密切的关系，因为数组类的元素类型（Element Type）如对象数组，最终还是要靠类加载器去创建。

关于数组类的加载创建过程是需要遵循如下规范的：

*   如果数组的元素类型是引用类型的话，那么就会递归采用前面内容中定义的类加载过程去加载这个元素类型，该数组本身将会在加载该元素类型的类加载器的**类名称空间**上被标识（这一点非常重要，在讲述类加载器的时候会介绍到，**一个类必须与类加载器一起确定唯一性**）。

*   如果数组的元素类型不是引用类型（例如 int[] 数组），JVM 则会把该 int[] 数组标记为启动类加载器（**Bootstrap Classloader**）关联。

实际上，在加载阶段虚拟机需要完成以下三件事：

*   通过一个类的全限定名来获取定义此类的二进制字节流。

*   将这个字节流所代表的静态存储结构转换为方法区（JDK1.8 以前）或者元数据（JDK1.8 以后）的运行时数据结构。

*   在内存中生成一个代表这个类的 java.lang.Class 对象，作为方法区或元数据区这个类的各种数据的访问入口。

加载阶段完成后，JVM 外部的二进制字节流就按照虚拟机所需要的格式存储在了方法区或元数据区的内存中了。

****|** 验证（Verification）**

验证是连接阶段的第一步，这一阶段的主要目的是为了确保 Class 文件的字节流中所包含的信息符合当前 JVM 的要求，并且不会危害 JVM 自身的安全。虚拟机如果不检查输入的字节流，对其完全信任的话，很可能会因为载入了有害的字节流而导致系统崩溃，所以验证阶段是非常重要的，这个阶段是否严谨，直接决定了 JVM 是否能承受恶意代码的攻击。

那么验证阶段具体应当检查哪些方面？如何检查？何时检查呢？

在**《Java 虚拟机规范（Java SE 7 版）》**中大概是有 130 页左右的篇幅是来描述验证的过程的，受篇幅所限，我们无法逐条规则去探讨。但从整体上来看，验证阶段大致会完成如下**四个阶段的检验**动作：

**1）、文件格式验证**

文件格式验证就是要验证字节流是否符合 Class 文件格式的规范，以及是否能被当前版本的虚拟机处理。例如，常量池的常量中是否有不被支持的常量类型；Class 文件中各个部分及文件本身是否有被删除的或附加的其他信息等等。

**这个阶段验证的主要目的就是为了保证输入的字节流能正确地解析并存储于方法区或元数据区之内**，格式上符合描述一个 Java 类型信息的要求。本阶段的验证是基于二进制字节流进行的，只有通过了这个阶段的验证，字节流才会正常进入内存（方法区／元数据区）中进行存储，所以后面剩下的 3 个验证阶段全部是基于已经载入内存的存储结构进行的，而不会再直接操作字节流了。

**2）、元数据验证**

元数据区验证的主要目的是对类的元数据信息进行语义的校验，以保证描述的信息符合 Java 语言规范的要求。

例如：

这个类是否有父类（除了 java.lang.Object 之外，所有的类都应该有父类）？这个类的父类是否继承了不允许被继承的类（如被 final 修饰的类）？如果这个类不是抽象类，是否实现了其父类或接口之中要求实现的所有方法？类中的字段、方法是否与父类产生矛盾？等等。虽然这些逻辑目前编译器都会在编译字节码文件时加以校验，但是作为 JVM 类加载本身为了确保自身的安全性，也是需要进行严格校验的。

**3）、字节码验证**

字节码验证是一个更加复杂的阶段，主要目的是通过数据流和控制流的分析，确定程序语义是合法的、符合逻辑的。在元数据验证阶段主要是完成了对元数据信息的类型校验，而这个阶段则是对类的方法体进行校验分析，确保被校验类的方法在运行时不会做出危害虚拟机安全的事件。例如，保证方法体中的类型转换是有效的，可以把一个子类对象赋值给父类的数据类型（上溯造型），但是不能把父类对象赋值给子类数据类型或者把对象赋值给与与它毫无继承关系的类型。

**4）、符号引用验证**

符号引用验证可以看做是对类自身以外（主要是常量池中的各种符号引用）的信息进行匹配性校验。目的是确保后面进入解析阶段后，解析动作能够正常执行。如果无法通过符号引用验证，就会抛出如 “java.lang.IllegalAccessError”、“java.lang.NoSuchFileIdError”、“java.lang.NoSuchMethodError” 等这样的异常信息。

对于 JVM 的类加载机制来说，验证阶段是一个非常重要，但是不一定必要（对程序的运行期没有影响）的阶段。如果所运行的全部代码，包括自己编写的以及第三方包中的代码都已经被反复使用和验证过，那么就可以考虑使用

“**-Xverify:none**” 参数来关闭大部分的验证措施，以缩短虚拟机类加载的时间。

****|** 准备（Preparation）**

准备阶段是正式为类变量（被 static 修饰的变量）分配内存并设置类变量初始值的阶段，这些变量所使用的内存都将在方法区（<Jdk1.8）元数据区（>=Jdk1.8）中进行分配。这时候进行内存分配的仅包括类变量，而不包括实例变量，实例变量将会在对象实例化的时候随对象一起分配在 Java 堆中。

另外，上面所说的对类变量进行初始值，通常情况下是初始为零值。如 int 类型的类变量，初始值就是 0。

****|** 解析（Resolution）**

在 class 文件被加载至 JVM 之前，这个类是无法知道其他类及方法、字段所对应的具体地址的，甚至不知道自己方法、字段的内存地址。因此，每当需要引用这些成员时 Java 编译器会生成一个**符号引用**。在运行阶段这个符号引用一般都能无歧义地定位在具体目标上。举个例子，对于一个方法调用，编译器会生成一个**包含目标方法所在类的名字、目标方法的名字、接收参数类型以及返回值类型的符号引用**，来指代所要调用的方法。

解析阶段的目的就是将这些符号引用解析成为**实际引用**。而实际引用就是真正指向**内存地址的指针、相对偏移量**或能间接定位到目标的**句柄**。解析动作主要针对**类或接口、字段、类方法、接口方法、方法类型、方法句柄**和**调用点限定符**这 7 类符号引用进行。

在前面我们提到过，解析阶段并不一定会在连接过程中完成，因为 JVM 虚拟机规范并没有对此作明确的要求，只是规定了：“**如果某些字节码使用了符号引用，那么在执行这些字节码之前，需要完成对这些符号引用的解析**”。对于这一点大家不要搞错了。

****|** 初始化（Intialization）**

类初始化是类加载过程的最后一步，是为标记为常量值的字段赋值，以及执行 <clinit> 方法的过程。那么什么样的字段才会被标记为常量值呢？<clinit > 方法又是什么呢？

在 Java 代码中如果要初始化一个静态字段，我们可以在声明时直接赋值，也可以在静态代码块中对其赋值。在这里，如果**直接赋值的静态字段被 final 所修饰，并且它的类型是基本类型或字符串时，那么该字段便会被 Java 编译器标记成常量值（ConstantValue）**，其初始化直接由 Java 虚拟机完成。

而除此之外的直接赋值操作，以及所有静态代码块中的代码则都会被 Java 编译器置于同一方法中，这个方法就是 **<clinit>** 方法，也称为**类构造器方法**。Java 虚拟机会通过加锁来确保类的 <clinit> 只会被执行一次。

在我们讲述 JVM 类加载过程的时候，并没有特别说明什么情况下需要开始类加载过程的第一个阶段：加载？这是因为 JVM 虚拟机规范并没有进行强制约束。但是对于初始化阶段，JVM 规范则是严格规定了发生如下情况时必须立刻对类进行 “初始化”，而加载、验证、准备也自然需要在此之前开始。

这几种情况如下：

> 1）、当虚拟机启动时，初始化用户指定的主类（包含 main 方法的类）；
> 
> 2）、当遇到用以新建目标类实例的 new 指令时，初始化 new 指令的目标类；
> 
> 3）、当遇到调用静态方法的指令时，初始化该静态方法所在的类；
> 
> 4）、当遇到访问静态字段的指令时，初始化该静态字段所在的类；
> 
> 5）、子类的初始化会先触发父类的初始化（如果父类还没有进行过初始化的话）；
> 
> 6）、如果一个接口定义了 default 方法，那么直接实现或者间接实现该接口的类的初始化，会触发该接口的初始化；
> 
> 7）、使用反射 API 对某个类进行反射调用时，初始化这个类；
> 
> 8）、当初次调用 MethodHandle 实例时（JDK1.7 的动态语言支持），初始化该 MethodHandle 指向的方法所在的类;

以上基本上就是类加载机制中初始化的大致过程，只有当初始化完成之后，类才能正式成为可执行的状态。

**类加载器**

在整个类加载过程中，除了在加载阶段用户应用程序可以通过自定义类加载器参与之外，其余的动作完全是由虚拟机主导和控制的。那么什么是类加载器呢？

在上述类加载机制的第一个阶段："加载" 中，把 “通过一个类的全限定名来获取描述此类的二进制字节流” 这个动作由 JVM 外部实现的代码模块称为“**类加载器”**。

从 JVM 的角度来看，只存在两种不同的类加载器：**启动类加载器（Bootstrap ClassLoader）、其他类的加载器**。启动类加载器是由 C++ 语言实现的，属于 JVM 自身的一部分，而其他的类加载器则都是独立于 JVM 外部，由 Java 语言实现的继承 java.lang.ClassLoader 的类型。

而从 Java 程序员的角度看，类加载器还可以划分得更加细致一些。示意图如下：

![](https://mmbiz.qpic.cn/mmbiz_png/l89kosVutolBMcGoCEkibLPKiabKzq1TiacKicNrFF7srxddy9kazrrkn5gibwulhtYAaMZ1pPxJMs9rusB1eKZic79Q/640?wx_fmt=png)

在上图中的类加载器，是有层次关系的，这种关系被称之为类加载器的 “**双亲委派模式**”，它要求除了顶层启动类加载器外，其余所有的类加载器都应当有自己的父类加载器，并且如果一个类加载器在收到类加载的请求之后都要先把这个请求委派给父类加载器去完成（每一个层次的类加载器都是如此，因此所有的加载请求最终都应该会传送到顶层的启动类加载器中），只有当父类加载器反馈自己无法完成这个加载请求（在搜索范围没有找到所需的类）时，子加载器才会尝试自己去加载。

**双亲委派模式不是强制性的约束模型**，只是 Java 设计者推荐给开发者的类加载器的实现方式，但是采用这种模式对于保证 Java 程序的稳定运作确实很重要的，因为它可以避免 Java 体系中基础的类型被混乱加载的风险。例如类 java.lang.Object，它存放在 rt.jar 之中，无论那一个类加载器要加载这个类，最终都会委派给启动类加载器，这样 Object 类在程序的各种类加载器环境中都是一个类，否则就会导致系统中出现多个不同的 Object 类，从而连 Java 类型体系中最基本的行为都无法保证。

以上就是关于 JVM 类加载系统的全部内容了，希望本文能够对你补充知识盲点起到一点作用！

<section style="max-width: 100%;box-sizing: border-box;color: rgb(51, 51, 51);font-family: -apple-system-font, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, &quot;PingFang SC&quot;, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;font-size: 17px;letter-spacing: 0.544px;text-align: justify;white-space: normal;line-height: 27.2px;overflow-wrap: break-word !important;">

<section class="" style="max-width: 100%;box-sizing: border-box;overflow-wrap: break-word !important;">

<section class="" style="max-width: 100%;box-sizing: border-box;overflow-wrap: break-word !important;">

<section class="" style="max-width: 100%;box-sizing: border-box;overflow-wrap: break-word !important;">

![](https://mmbiz.qpic.cn/mmbiz_gif/3cfNeMrD2uxs9ynEZCs0yrUOibg6er2Ir53KeWj1J7pAcqU3rLKN9CPEOxlGOENo0vI69CQkgJo1mk789WzDbQQ/640?wx_fmt=gif)

</section>

</section>

</section>

</section>

<section style="max-width: 100%;box-sizing: border-box;color: rgb(51, 51, 51);font-family: -apple-system-font, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, &quot;PingFang SC&quot;, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;font-size: 17px;letter-spacing: 0.544px;text-align: justify;white-space: normal;line-height: 27.2px;overflow-wrap: break-word !important;">

<section class="" style="max-width: 100%;box-sizing: border-box;overflow-wrap: break-word !important;">

<section class="" style="max-width: 100%;box-sizing: border-box;overflow-wrap: break-word !important;">

<section class="" style="max-width: 100%;box-sizing: border-box;overflow-wrap: break-word !important;">

推荐阅读：

[一张图看懂 JVM（升级版）](http://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483820&idx=1&sn=8418f0f6a618bb0f0ca0980af09a816f&chksm=fd2fd06eca5859786ab124dd204a7ec9b1ad3ed230b9b531086cc6729a277a05d3e8307b7e0d&scene=21#wechat_redirect)
[一张图看懂 JVM 之垃圾回收算法详解](http://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247483998&idx=1&sn=5ae39fc0587c954378c4c389ad8bbe8a&chksm=fd2fd39cca585a8a3b3aafee498a2cd1ddad80f61745ca29f0ec79be7489d45516ddbb4e27b0&scene=21#wechat_redirect)
[一张图看懂 JVM 之垃圾回收器详解](http://mp.weixin.qq.com/s?__biz=MzU3NDY4NzQwNQ==&mid=2247484016&idx=1&sn=d1e1ccef1760a5567d3d0709d3986c35&chksm=fd2fd3b2ca585aa49d636ad369f1cc2206cd87ece94d056d1784e49bcdb7e94bfd0e2b307d7c&scene=21#wechat_redirect)
</section>

</section>

</section>

</section>

<section class="" style="max-width: 100%;font-family: -apple-system-font, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, &quot;PingFang SC&quot;, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;text-align: justify;white-space: normal;background-color: rgb(255, 255, 255);font-size: 16px;letter-spacing: 0px;color: rgb(62, 62, 62);line-height: 1.6;box-sizing: border-box !important;word-wrap: break-word !important;">

<section class="" style="max-width: 100%;line-height: 1.6;letter-spacing: 0px;box-sizing: border-box !important;word-wrap: break-word !important;">

<section class="" style="max-width: 100%;line-height: 1.6;letter-spacing: 0px;box-sizing: border-box !important;word-wrap: break-word !important;">

<section class="" style="max-width: 100%;line-height: 1.6;letter-spacing: 0px;box-sizing: border-box !important;word-wrap: break-word !important;">

<section class="" style="max-width: 100%;line-height: 1.6;letter-spacing: 0px;box-sizing: border-box !important;word-wrap: break-word !important;">

<section class="" style="max-width: 100%;line-height: 1.6;letter-spacing: 0px;box-sizing: border-box !important;word-wrap: break-word !important;">

—————END—————

![](https://mmbiz.qpic.cn/mmbiz_jpg/l89kosVuton5KOpE65Uoh7kf3PZ9yRbtyBMAuSscgOFicWLHWWO8KBIibf8WeI7lqvrbr9SOs8wnVteDUwLg2YicA/640?wx_fmt=jpeg)

</section>

</section>

</section>

</section>

</section>

</section>

长按识别图片二维码，关注 “无敌码农” 获取更多精彩内容