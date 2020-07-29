package com.bins.rpc.remoting.transport.netty.server;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.enums.RpcConfigEnum;
import com.bins.rpc.factory.SingletonFactory;
import com.bins.rpc.provider.ServiceProvider;
import com.bins.rpc.provider.ServiceProviderImpl;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.remoting.transport.netty.codec.kryo.NettyKryoDecoder;
import com.bins.rpc.remoting.transport.netty.codec.kryo.NettyKryoEncoder;
import com.bins.rpc.serialize.kryo.KryoSerializer;
import com.bins.rpc.utils.PropertiesFileUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author leo-bin
 * @date 2020/7/28 0:25
 * @apiNote rpc服务端
 */
@Slf4j
public class NettyRpcServer {

    public static int defaultPort = 9000;
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    private final KryoSerializer kryoSerializer = new KryoSerializer();

    public NettyRpcServer() {
    }

    /**
     * 发布并实例化服务
     */
    public <T> void registerService(T service, Class<T> serviceClass, RpcServiceProperties serviceProperties) {
        serviceProvider.publishService(service, serviceClass, serviceProperties);
    }

    /**
     * 启动rpc服务端
     */
    @SneakyThrows
    public void start() {
        //获取本地host
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(workerGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //空闲连接检测，30s内没有收到客户端请求，就关闭通道
                            ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            //同步等待绑定端口
            ChannelFuture future = serverBootstrap.bind(host, defaultPort).sync();

            //一旦服务端绑定的端口关闭了就结束future
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
