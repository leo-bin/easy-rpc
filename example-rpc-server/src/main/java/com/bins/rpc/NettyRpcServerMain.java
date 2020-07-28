package com.bins.rpc;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.remoting.transport.netty.server.NettyRpcServer;
import com.bins.rpc.serviceimpl.ScienceCalculator;
import com.bins.rpc.serviceimpl.StandardCalculator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo-bin
 * @date 2020/7/28 17:01
 * @apiNote 基于netty进行通信的rpc服务端测试
 */
@Slf4j
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
        //4.发布服务
        server.registerService(calculator, Calculator.class, serviceProperties);
    }

    public static void scienceTest(NettyRpcServer server) {
        //1.创建服务实例
        Calculator calculator = new ScienceCalculator();
        //2.创建服务专有属性(接口名，标签)
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("science").build();
        //4.发布服务
        server.registerService(calculator, Calculator.class, serviceProperties);
    }
}
