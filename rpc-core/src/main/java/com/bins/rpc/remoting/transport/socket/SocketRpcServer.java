package com.bins.rpc.remoting.transport.socket;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.factory.SingletonFactory;
import com.bins.rpc.provider.ServiceProvider;
import com.bins.rpc.provider.ServiceProviderImpl;
import com.bins.rpc.utils.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author leo-bin
 * @date 2020/7/23 18:56
 * @apiNote 基于socket中的BIO实现服务端的通信
 */
@Slf4j
public class SocketRpcServer {

    private ExecutorService threadPool;
    private String host;
    private int port;
    private ServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;


    public SocketRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactoryUtil.createThreadPoolIfAbsent("socket-server-rpc-pool");

        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }


    /**
     * 发布服务
     */
    public <T> void publishService(T service, Class<T> serviceClass, RpcServiceProperties serviceProperties) {
        //注册一个服务提供者实例
        serviceProvider.publishService(service, serviceClass, serviceProperties);
        log.info("服务：{}已经注册成功。", serviceProperties.getUniqueServiceName());

        //对外发布服务
        log.info("服务：{}已经发布成功。", serviceProperties.getUniqueServiceName());
    }


    /**
     * 启动服务
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            //监听端口
            serverSocket.bind(new InetSocketAddress(host, port));

            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new RpcRequestHandlerTask(socket));
            }
            //关闭当前的线程池
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur exception ", e);
        }
    }


}
