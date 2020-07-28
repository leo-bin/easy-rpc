package com.bins.rpc;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.remoting.transport.socket.SocketRpcServer;
import com.bins.rpc.serviceimpl.ScienceCalculator;
import com.bins.rpc.serviceimpl.StandardCalculator;

/**
 * @author leo-bin
 * @date 2020/7/23 22:40
 * @apiNote 基于socket的rpc服务端测试
 */
public class SocketRpcServerMain {
    public static void main(String[] args) {
        //绑定具体的服务地址
        SocketRpcServer server = new SocketRpcServer("127.0.0.1", 9000);

        //启动所有服务
        standardTest(server);
        scienceTest(server);

        //开始监听服务
        server.start();
    }


    public static void standardTest(SocketRpcServer server) {
        //1.创建服务实例
        Calculator calculator = new StandardCalculator();
        //2.创建服务专有属性(接口名，标签)
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("standard").build();
        //4.发布服务
        server.publishService(calculator, Calculator.class, serviceProperties);
    }

    public static void scienceTest(SocketRpcServer server) {
        //1.创建服务实例
        Calculator calculator = new ScienceCalculator();
        //2.创建服务专有属性(接口名，标签)
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("science").build();
        //4.发布服务
        server.publishService(calculator, Calculator.class, serviceProperties);
    }
}
