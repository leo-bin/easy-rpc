# easy-rpc

## 前话

**参考dubbo以及部分开源项目实现了一个简单自定义RPC框架——easy-rpc**

**开发本项目的目的就是为了在手写的过程中更好的理解和学习框架的设计原理。**

------------------------------------------------------------



## 目前的整体架构



**`技术栈：`**

**1.使用`ZooKeeper`作为服务的注册中心**

**2.使用`Netty`作为RPC调用的通信方式**

**3.使用`Kryo`代替JDK自带的序列化机制**

**4.使用`JDK动态代理`实现网络通信的底层封装过程**

**5.提供多种`负载均衡`算法。**





**架构图：**

![image-20200730181003042](https://bins-pic.oss-cn-shanghai.aliyuncs.com/mypic/image-20200730181003042.png)







## 项目开发的初衷

为什么要做这个项目？为什么要反复造轮子？现成的dubbo不香吗？

现有的rpc框架其实已经做的很好了，像阿里的dubbo，新浪的motan等等。

但是本人觉得在学习一门技术之前如果能够深入实践一下，会比学习理论知识要好的多。

实践之后不仅印象会深刻一些，你还能从中学习到如何更优雅的设计某个功能？

实践能够让你发现一些在使用和学习过程中发现不了的事情，比如某个接口应该怎么样设计？

怎么设计会比较优雅，可扩展性怎么样？代码之间的耦合度又是怎样的？

这样设计线程安全吗？这样写会不会比较重复？

**总知一句话，光会用还不行，一定要深入理解设计原理。**







## 目前已经实现的功能

1.整个项目的骨架已经完成，基本的RPC调用都没问题。

2.服务端配置了线程池，采用多线程去处理客户端的请求。

3.写好了一套基于BIO的Socket进行通信的代理模板。

4.通过服务名+服务标签的组合实现了服务名的唯一，解决了单接口多个实现类的问题。

5.使用Kryo代替了JDK自带的序列化，提高序列化效率。

6.使用Netty的NIO代替了原来的基于BIO的Socket进行网络通信。

7.使用ZooKeeper完成了服务注册中心，并使用完全随机算法实现负载均衡。

----------------------------------------------------------




## 接下来的优化方向

1.RPC之间的通信用异步去做。**已解决✔**

2.加上服务中心，可以用ZooKepper或者Redis实现。**已解决✔**

3.解决一个接口但是有多个实现类如何实现服务的注册。**已解决✔**

4.序列化能不能不使用JDK自带的序列化机制？**已解决✔**

5.网络通信模式能不能使用NIO？可以使用netty重构。**已解决✔**

6.有了服务中心可以加上负载均衡。**已解决✔**

7.服务暴露（注册）使用注解完成。

8.。。。











## 如何使用？

1.先克隆项目到本地

```shell
git clone git@github.com:leo-bin/easy-rpc.git
```



2.或者使用GUI工具：

找一个文件夹，右键鼠标，点击OK，开始克隆：

![image-20200730182126836](https://bins-pic.oss-cn-shanghai.aliyuncs.com/mypic/image-20200730182126836.png)



**3.使用IDEA导入项目（选择Maven导入方式）**



**4.项目骨架：**

![image-20200730181741950](https://bins-pic.oss-cn-shanghai.aliyuncs.com/mypic/image-20200730181741950.png)



一共有五个模块：

**rpc-core**是核心模块，主要处理代理，服务注册和发现，网络通信等等。

**rpc-common**是公共模块，主要放一些公共组件，比如说全局使用的工具包和配置类。

**rpc-api**是服务接口，用来增加服务接口。

剩下的两个就是测试模块了，一个是**客户端**，一个是**服务端**。



假设我们现在有一个服务叫做：Calculator，用来计算普通的加法。

但是现在需要将这个服务作为服务端单独部署在一台服务器中。

现在有另外一台服务器作为客户端需要调用这个服务。





首先我们需要在api模块中提前写好服务的接口：

```java
package com.bins.rpc;

//计算器服务接口
public interface Calculator {
    /**
     * 加法运算
     */
    Integer add(int a, int b);
}
```



然后我们需要在服务端模块写好接口的实现类

假设有两种计算器：**标准计算器**和**科学计算器**

```java
package com.bins.rpc.serviceimpl;

import com.bins.rpc.Calculator;

//标准计数器
public class StandardCalculator implements Calculator {
    @Override
    public Integer add(int a, int b) {
        return a + b;
    }
}
```



```java
package com.bins.rpc.serviceimpl;

import com.bins.rpc.Calculator;

//科学计数器
public class ScienceCalculator implements Calculator {
    @Override
    public Integer add(int a, int b) {
        return a + b;
    }
}
```



需要提前在电脑中安装ZooKeeper并启动，Windows或者Llinux都可以（我是用的Windows）

**然后我们写好配置文件：**

```properties
# 配置关于zk的属性
rpc.zookeeper.address=127.0.0.1:2181

#负载均衡策略，1是完全随机，2是完全轮询策略
rpc.loadBalance.type=2
```



现在我们需要将服务注册进ZK中，然后启动服务端：

```java
public class NettyRpcServerMain {
    public static void main(String[] args) {
        System.out.println("//////////////////////////////////////////////////////////////////");
        System.out.println("//////////////////////////////////////////////////////////////////");
        System.out.println("################  这是基于netty通信的rpc服务端测试  ################");
        System.out.println("//////////////////////////////////////////////////////////////////");
        System.out.println("//////////////////////////////////////////////////////////////////");
        
        NettyRpcServer nettyRpcServer = new NettyRpcServer();

        //注册服务
        standardTest(nettyRpcServer);
        scienceTest(nettyRpcServer);

        //启动rpc服务
        nettyRpcServer.start();
    }

    public static void standardTest(NettyRpcServer server) {
        //1.创建服务实例
        Calculator calculator = new StandardCalculator();
        //2.创建服务专有属性(接口名，标签)
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("standard").build();
        //3.发布服务
        server.registerService(calculator, Calculator.class, serviceProperties);
    }

    public static void scienceTest(NettyRpcServer server) {
        //1.创建服务实例
        Calculator calculator = new ScienceCalculator();
        //2.创建服务专有属性(接口名，标签)
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("science").build();
        //3.发布服务
        server.registerService(calculator, Calculator.class, serviceProperties);
    }
}
```



**启动效果：**

![image-20200730183645533](https://bins-pic.oss-cn-shanghai.aliyuncs.com/mypic/image-20200730183645533.png)



**现在写客户端的测试代码：**

```java
@Slf4j
public class NettyRpcClientMain {
    public static void main(String[] args) {
        System.out.println("//////////////////////////////////////////////////////////////////");
        System.out.println("//////////////////////////////////////////////////////////////////");
        System.out.println("################  这是基于netty通信rpc客户端测试  ####################");
        System.out.println("//////////////////////////////////////////////////////////////////");
        System.out.println("//////////////////////////////////////////////////////////////////");

        //选择通信方式：netty
        ClientTransport clientTransport = new NettyRpcClient();

        //选择服务并开始调用
        standardTest(clientTransport);
        scienceTest(clientTransport);
    }

    public static void standardTest(ClientTransport clientTransport) {
        //设置服务专属属性
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("standard").build();

        //拿到代理对象开始执行业务
        Calculator calculator = ProxyFactory.createProxy(Calculator.class, clientTransport, serviceProperties);
        int result = calculator.add(1, 1);
        log.info("使用标准计数器计算：1+1={}", result);
    }

    public static void scienceTest(ClientTransport clientTransport) {
        //设置服务专属属性
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("science").build();

        //拿到代理对象开始执行业务
        Calculator calculator = ProxyFactory.createProxy(Calculator.class, clientTransport, serviceProperties);
        int result = calculator.add(1, 1);
        log.info("使用科学计数器计算：1+1={}", result);
    }
}
```



**测试效果：**

![image-20200730184336800](https://bins-pic.oss-cn-shanghai.aliyuncs.com/mypic/image-20200730184336800.png)







## 参考和感谢

首先是受到了**Snailclimb**提供的rpc项目的启发（也就是我们熟悉的**guide**哥，非常感谢）

**项目地址：**   https://github.com/Snailclimb/guide-rpc-framework





**其次就是一些在学习RPC的时候发现的博文：**

1.https://www.jianshu.com/p/2accc2840a1b

2.https://www.jianshu.com/p/5b90a4e70783













