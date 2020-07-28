package com.bins.rpc;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.proxy.ProxyFactory;
import com.bins.rpc.remoting.transport.ClientTransport;
import com.bins.rpc.remoting.transport.netty.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo-bin
 * @date 2020/7/28 17:01
 * @apiNote 基于netty进行通信的rpc客户端测试
 */
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
