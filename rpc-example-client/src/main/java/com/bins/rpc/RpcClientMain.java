package com.bins.rpc;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.proxy.ProxyFactory;
import com.bins.rpc.remoting.transport.ClientTransport;
import com.bins.rpc.remoting.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;


/**
 * @author leo-bin
 * @date 2020/7/23 22:53
 * @apiNote 客户端测试
 */
@Slf4j
public class RpcClientMain {
    public static void main(String[] args) {
        //1.选择通信方式
        ClientTransport clientTransport = new SocketRpcClient();
        standardTest(clientTransport);
        scienceTest(clientTransport);
    }


    public static void standardTest(ClientTransport clientTransport) {
        //2.选择服务专属属性
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("standard").build();
        //4.拿到代理对象开始执行业务
        Calculator calculator = ProxyFactory.createProxy(Calculator.class, clientTransport, serviceProperties);
        int result = calculator.add(1, 1);
        log.info("标准计数器计算：1+1={}", result);
    }


    public static void scienceTest(ClientTransport clientTransport) {
        //2.选择服务专属属性
        RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                .serviceName(Calculator.class.getCanonicalName())
                .tag("science").build();
        //4.拿到代理对象开始执行业务
        Calculator calculator = ProxyFactory.createProxy(Calculator.class, clientTransport, serviceProperties);
        int result = calculator.add(1, 1);
        log.info("科学计数器计算：1+1={}", result);
    }
}
