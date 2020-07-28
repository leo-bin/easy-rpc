package com.bins.rpc.remoting.transport.netty.client;

import com.bins.rpc.factory.SingletonFactory;
import com.bins.rpc.registry.StaticServiceDiscovery;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.remoting.transport.ClientTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author leo-bin
 * @date 2020/7/28 0:24
 * @apiNote 使用netty进行通信的rpc客户端
 */
@Slf4j
public class NettyRpcClient implements ClientTransport {

    private final ChannelProvider channelProvider;
    private final UnprocessedRequests unprocessedRequests;
    private final StaticServiceDiscovery serviceDiscovery;

    public NettyRpcClient() {
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        serviceDiscovery = new StaticServiceDiscovery();
    }


    @Override
    public CompletableFuture<RpcResponse<Object>> sendRpcObject(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        String serviceName = rpcRequest.getServiceProperties().getUniqueServiceName();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(serviceName);

        //获取一个可用的channel
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel != null && channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //开始写数据
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }
}
