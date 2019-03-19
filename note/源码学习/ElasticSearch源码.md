### ElasticSearch 源码

#### 1、源码下载与导入安装

`git clone https://github.com/elastic/elasticsearch.git` 通过 Git 下载源码

源码下载好后，再切换想看的版本 Tag

通过 gradlew idea 初始化，期间可以修改 elasticsearch 源码里的 gradle.properties 文件，添加代理，以便于加快速度，配置如下：

`systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=1080
systemProp.https.nonProxyHosts=10.*|localhost
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=1080
systemProp.https.nonProxyHosts=10.*|localhost`

然后打开 Idea，导入代码即可。

导入后，根据想要看的版本，下载官方发行版，以便于从idea启动。

Idea中，启动 org.elasticsearch.bootstrap.Elasticsearch 类，并且添加如下启动参数：

`-Des.path.conf=E:\database\elasticsearch\6.6.2\config
-Des.path.home=E:\database\elasticsearch\6.6.2
-Dlog4j2.disable.jmx=true
-Djava.security.policy=E:\database\elasticsearch\6.6.2\java.policy`

在idea中的启动配置里，记得将 ***Include dependencies with Provided scope*** 这个选项勾上。

此外，还会遇到一个问题 ：

``` java
org.elasticsearch.bootstrap.StartupException: java.security.AccessControlException: access denied ("java.lang.RuntimePermission" "createClassLoader")
```

这里有2种解决办法：

第一种： 在发行版ElasticSearch目录下（或者其他自定义目录）新建 java.policy 文件，填入下面内容


``` java
grant {
    permission java.lang.RuntimePermission "createClassLoader";
};
// 然后在 VM options 加入 java.security.policy 的设置，指向该文件即可
// -Djava.security.policy=E:\database\elasticsearch\6.6.2\java.policy
```

第二种： 就是在 **%JAVA_HOME%\conf\security** 目录下（JDK10是这个路径，之前的版本不确定），我的目录是 C:\Program Files\Java\jdk-10.0.2\conf\security，打开 java.policy 文件，在 grant 中加入下面这句，赋予权限：

``` java
permission java.lang.RuntimePermission "createClassLoader";
```
