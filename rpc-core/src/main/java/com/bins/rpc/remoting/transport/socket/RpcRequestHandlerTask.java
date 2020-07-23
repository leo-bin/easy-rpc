package com.bins.rpc.remoting.transport.socket;

import com.bins.rpc.factory.SingletonFactory;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.remoting.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author leo-bin
 * @date 2020/7/23 18:57
 * @apiNote 处理rpc请求任务
 */
@Slf4j
public class RpcRequestHandlerTask implements Runnable {

    private Socket socket;
    private RpcRequestHandler requestHandler;

    public RpcRequestHandlerTask(Socket socket) {
        this.socket = socket;
        requestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }


    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            //1.读取请求
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();

            //2.处理请求
            Object result = requestHandler.handle(rpcRequest);

            //3.返回请求结果
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception", e);
        }
    }
}
