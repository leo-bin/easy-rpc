package com.bins.rpc.proxy.handler;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.remoting.dto.RpcMessageChecker;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.remoting.transport.ClientTransport;
import com.bins.rpc.remoting.transport.netty.client.NettyRpcClient;
import com.bins.rpc.remoting.transport.socket.SocketRpcClient;
import com.bins.rpc.utils.CommonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * @author leo-bin
 * @date 2020/7/23 18:12
 * @apiNote Rpc调用的代理类处理器，封装对序列化和反序列化以及网络请求的底层实现
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler {

    private final ClientTransport clientTransport;
    private final RpcServiceProperties serviceProperties;


    public RpcInvocationHandler(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
        this.serviceProperties = RpcServiceProperties.builder().serviceName("").tag("").build();
    }

    public RpcInvocationHandler(ClientTransport clientTransport, RpcServiceProperties serviceProperties) {
        this.clientTransport = clientTransport;

        if (serviceProperties.getTag() == null) {
            serviceProperties.setTag("");
        }
        this.serviceProperties = serviceProperties;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
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
                .serviceProperties(serviceProperties)
                .build();

        RpcResponse rpcResponse = null;

        //2.发送请求(选择哪种通信方式：基于NIO的netty或者BIO的socket)
        //socket
        if (clientTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse) clientTransport.sendRpcObject(rpcRequest);
        }

        //netty
        if (clientTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> future = (CompletableFuture<RpcResponse<Object>>)
                    clientTransport.sendRpcObject(rpcRequest);
            //如果还没结果则原地阻塞直到返回调用结果
            rpcResponse = future.get();
        }

        //3.校验响应结果
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
