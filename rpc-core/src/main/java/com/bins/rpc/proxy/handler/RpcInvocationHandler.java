package com.bins.rpc.proxy.handler;

import com.bins.rpc.remoting.dto.RpcMessageChecker;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.remoting.transport.ClientTransport;
import com.bins.rpc.remoting.transport.socket.RpcSocketClient;
import com.bins.rpc.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author leo-bin
 * @date 2020/7/23 18:12
 * @apiNote Rpc调用的代理类处理器，封装对序列化和反序列化以及网络请求的底层实现
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler {

    private final ClientTransport clientTransport;

    public RpcInvocationHandler(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());

        //1.封装请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(CommonUtil.UUID32())
                .build();

        RpcResponse rpcResponse = null;

        //2.发送请求
        if (clientTransport instanceof RpcSocketClient) {
            rpcResponse = (RpcResponse) clientTransport.sendRpcObject(rpcRequest);
        }

        //3.校验响应结果
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}