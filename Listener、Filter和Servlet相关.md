## Listener、Servlet、Filter启动顺序及其在web.xml中的相关参数
## 一、Listener

Listener是用来监听ServletContext、Session和Request的创建、改变、销毁等事件。对于Session还有激活、钝化、绑定和解绑等事件的监听。

Listener都可以拦截到ServletContext，ServletContext是个全局唯一的对象，通过它能传递各种数据。

到目前最新的Servlet标准，Listener分为3类： ServletContext，HttpSession和ServletRequest相关的Listener。

###  1.ServletContext相关的Listener
 
 该类Listener有 ServletContextListener、ServletContextAttributeListener。
 
#### ServletContextListener接口方法有：
  - `contextInitialized(ServletContextEvent e)`  
  - `contextDestroyed(ServletContextEvent e)`
 
#### ServletContextAttributeListener接口方法有:
 - `attributeAdded(ServletContextAttributeEvent e)`
 - `attributeRemoved(ServletContextAttributeEvent e)`
 - `attributeReplaced(ServletContextAttributeEvent e)`

### 2.HttpSession相关的Listener
 
 该类Listener有：
 
 - HttpSessionListener
 - HttpSessionActivationListener
 - HttpSessionAttributeListener
 - HttpSessionBindingListener
 
 #### HttpSessionListener的接口方法有：

 - `sessionCreated(HttpSessionEvent e)` 
 - `sessionDestroyed(HttpSessionEvent e)`
这个Listener可以监听到Session创建和销毁，所以一个简单的应用就是统计创建Session的人数。

 #### HttpSessionActivationListener的接口方法有：
  - `sessionDidActivate(HttpSessionEvent e)`
  - `sessionWillPassivate(HttpSessionEvent e)`
 HttpSessionActivationListener是每个满足JSP/Servlet协议的Web容器都需要实现的。Web容器可以不直接实现支持分布式应用，但需要实现这个接口。
 P.S.：在Jetty容器中需要这样做：session.setAttribute("name", YourImplementClass)。在Tomcat中需要实现Serilizable才能让该接口实现类起作用。
 
 
 #### HttpSessionAttributeListener的接口方法有：
- `attributeAdded(HttpSessionBindingEvent e)`
- `attributeRemoved(HttpSessionBindingEvent e)`
- `attributeReplaced(HttpSessionBindingEvente)`

 #### HttpSessionBindingListener的接口方法有：
- `valueBound(HttpSessionBindingEvent e)`
- `valueUnbound(HttpSessionBindingEvent e)`

### 3.ServletRequest相关的Listener

该类Listener有：

- ServletRequestListener
- ServletRequestAttributeListener

#### ServletRequestListener的接口方法有：
 - `void requestDestroyed(ServletRequestEvent e) `
 - `void requestInitialized(ServletRequestEvent e) `
 
    通过这个Listener可以监听到每个由该应用处理的每个请求，并拦截下ServletRequest。

#### ServletRequestAttributeListener的接口方法有:
-  `void attributeAdded(ServletRequestAttributeEvent e) `
-  `void attributeRemoved(ServletRequestAttributeEvent e) `
-  `void attributeReplaced(ServletRequestAttributeEvent e)`

### 4.各种Listener的调用时机
当配置多个Listener时，Listener的初始化是按照在Web.xml中声明的顺序初始化的。当有多个相同Listener时，由于他们监听同一个事件，因此得有一个调用顺序。在Servlet3.0前的标准，多个Listener监听到同一个事件时，是随机调用多个Listener中的响应方法的。在Servlet3.0及之后的标准中，这个顺序是按照Web.xml中配置的顺序来调用的，当然这是大多数Listener的调用的方式，HttpSessionListener的 sessionDestroy 方法是与Web.xml中配置顺序相反的调用。

ServletContextListener 在Web应用启动时就会调用其 contextInitialized 方法。

ServletRequestListener 在Web应用接收到请求时创建或者销毁 Request 时响应 （它比Filter的范围更广，因为它会监听所有 URL 的请求，Filter 只会响应它所对应的 URL 请求）。

HttpSessionListener 在Web应用创建或者销毁一个 Session 时响应。

HttpSessionActivateListener 在Web应用持久化Session时的前后分别调用 sessionWillPassivate、sessionDidActivate。

HttpSessionBindingListener 在 session.setAttribute 和 session.removeAttribute 的时候响应。

ServletContextAttributeListener 、HttpSessionAttributeListener、ServletRequestAttributeListener 都是在监听各自所监听的对象在其存放的键值对，当键值对发生增删改的时候响应。

## 二、Filter
 Filter是起过滤作用的，比Servlet先调用（调用doFilter方法）。一个应用中可能会有多个Filter，每个Filter按照Web.xml中配置的顺序链式处理，即 Filter1--->Filter2--->Filter3...，这样处理下去，但每个Filter需要再doFilter中调用filterChain.doFilter(request, response)让下个Filter处理。

 Filter在web.xml中配置的参数有：
 - `<init-param></init-param>`
 - `<async-supported>(Servlet 3.0后支持的是否开启异步处理设置)`

 指定过滤类型（如果不配置，则默认为request）：
 - `<dispatcher>REQUEST</dispatcher> （过滤所有客户端请求，不会处理内部跳转）` 
 - `<dispatcher>FORWARD</dispatcher> （过滤内部跳转 不包括include方式跳转）`
 - `<dispatcher>INCLUDE</dispatcher>  （只过滤include内部跳转。Include也是跳转的一种，但是A调用B后，还会回来调用A）`
     在Filter中的init方法中可以获取到 InitParameter、ServletContext 。

 Filter的接口方法有：
 - `void init(FilterConfig fConfig)`
 - `void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)`
 - `void destroy()`

   其生命周期是 Filter实现类的构造方法---> init() ---> destroy()。doFilter方法会在每次服务器接收到请求时调用（如果Filter有映射到请求地址）。

