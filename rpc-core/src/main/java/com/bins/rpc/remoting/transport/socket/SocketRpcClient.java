package com.bins.rpc.remoting.transport.socket;

import com.bins.rpc.exception.RpcException;
import com.bins.rpc.registry.ServiceDiscovery;
import com.bins.rpc.registry.staticservice.StaticServiceDiscovery;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.transport.ClientTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author leo-bin
 * @date 2020/7/23 18:56
 * @apiNote 基于socket实现客户端的通信
 */
public class SocketRpcClient implements ClientTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = new StaticServiceDiscovery();
    }


    @Override
    public Object sendRpcObject(RpcRequest rpcRequest) {
        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            //1.建立连接
            socket.connect(address);

            //2.通过socket拿到一个输出流并进行输出（输出的时候就已经帮我们实现了序列化了）
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);

            //3.同样也是通过socket拿到一个输入流，读取传输过来的数据
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            //4.返回请求结果
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败", e);
        }
    }
}
