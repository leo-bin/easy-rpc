package com.bins.rpc;

import com.bins.rpc.proxy.ProxyFactory;
import com.bins.rpc.proxy.handler.RpcInvocationHandler;
import com.bins.rpc.remoting.transport.ClientTransport;
import com.bins.rpc.remoting.transport.socket.RpcSocketClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo-bin
 * @date 2020/7/23 22:53
 * @apiNote 客户端测试
 */
@Slf4j
public class RpcClientMain {

    public static void main(String[] args) {
        ClientTransport clientTransport = new RpcSocketClient();
        Calculator calculator = ProxyFactory.createProxy(Calculator.class, new RpcInvocationHandler(clientTransport));
        int result = calculator.add(1, 1);
        log.info("1+1={}", result);
    }
}
