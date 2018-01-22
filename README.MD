
### 介绍

带web展示的离线GC分析器，可本地启动当做一个本地软件使用，亦可部署在公司服务器作为远程小文件分析网站app。

### gc 日志参数

~~~ shell
-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/path/to/gc.log
~~~

目前暂时仅支持CMS，未来支持G1。