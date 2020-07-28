package com.bins.rpc.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leo-bin
 * @date 2020/7/28 1:06
 * @apiNote 全局的Channel提供者，通过单例保证每一个地址的连接管道只有一个
 */
@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;
    private final NettyClient nettyClient;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>(16);
        nettyClient = new NettyClient();
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        //用服务器地址做key
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            //检查channel是否可用
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(channel);
            }
        }
        //没有就创建新的连接管道
        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channelMap.put(key, channel);
        return channel;
    }


    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map size :[{}]", channelMap.size());
    }
}
