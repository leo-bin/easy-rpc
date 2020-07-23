package com.bins.rpc;

import com.bins.rpc.remoting.transport.socket.RpcSocketServer;

/**
 * @author leo-bin
 * @date 2020/7/23 22:40
 * @apiNote rpc服务端测试
 */
public class RpcServerMain {
    public static void main(String[] args) {
        //1.创建服务实例
        Calculator calculator = new CalculatorImpl();
        //2.找到具体服务地址并发布服务
        RpcSocketServer server = new RpcSocketServer("127.0.0.1", 9000);
        server.publishService(calculator, Calculator.class);
    }
}
