##Listener、Servlet、Filter启动顺序及其在web.xml中的相关参数

###一、 Listener

Listener是用来监听ServletContext、Session和Request的创建、改变、销毁等事件。对于Session还有激活、钝化、绑定和解绑等事件的监听。

到目前最新的Servlet标准，Listener分为3类： ServletContext，HttpSession和ServletRequest相关的Listener。

 1.  ServletContext相关的Listener
 
 该类Listener有 ServletContextListener、ServletContextAttributeListener。
 
 ServletContextListener接口方法有：
 `contextInitialized(ServletContextEvent e)` 
 `contextDestroyed(ServletContextEvent e)`
通过实现这个接口，我们可以拦截到ServletContext。ServletContext是全局唯一的。

 ServletContextAttributeListener接口方法有:
`attributeAdded(ServletContextAttributeEvent e)`
`attributeRemoved(ServletContextAttributeEvent e)`
`attributeReplaced(ServletContextAttributeEvent e)`

 2.  HttpSession相关的Listener
 
 该类Listener有：HttpSessionListener、HttpSessionActivationListener、HttpSessionAttributeListener、HttpSessionBindingListener。 
 
 HttpSessionListener的接口方法有：
 `sessionCreated(HttpSessionEvent e)`
 `sessionDestroyed(HttpSessionEvent e)`
 
 HttpSessionActivationListener的接口方法有：
 `sessionDidActivate(HttpSessionEvent e)`
 `sessionWillPassivate(HttpSessionEvent e)`
 这个接口实现的后，在Web.xml中注册，还得在session中注册，例如：session.setAttribute
 
 HttpSessionAttributeListener的接口方法有：
`attributeAdded(HttpSessionBindingEvent e)`
`attributeRemoved(HttpSessionBindingEvent e)`
`attributeReplaced(HttpSessionBindingEvente)`

 HttpSessionBindingListener的接口方法有：
`valueBound(HttpSessionBindingEvent e)`
`valueUnbound(HttpSessionBindingEvent e)`

 HttpSessionActivationListener是每个满足JSP/Servlet协议的Web容器都需要实现的。Web容器可以不直接实现支持分布式应用，但需要实现这个接口。

##二、 Filter
 Filter是起过滤作用的，比Servlet先调用（调用doFilter方法）。一个应用中可能会有多个Filter，每个Filter按照Web.xml中配置的顺序链式处理，即 Filter1--->Filter2--->Filter3...，这样处理下去，但每个Filter需要再doFilter中调用filterChain.doFilter(request, response)让下个Filter处理。

 Filter在web.xml中配置的参数有`<init-param>`，`<async-supported>(Servlet 3.0后支持的是否开启异步处理设置)`。在Filter中的init方法中可以获取到 InitParameter、ServletContext 。

 Filter的接口方法有：
 `void init(FilterConfig fConfig)`
 `void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)`
 `void destroy()`
 其生命周期是 Filter实现类的构造方法---> init() ---> destroy()。doFilter方法会在每次服务器接收到请求时调用如果Filter有映射到请求地址。

