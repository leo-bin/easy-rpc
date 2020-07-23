package com.bins.rpc.remoting.transport;

import com.bins.rpc.remoting.dto.RpcRequest;

/**
 * @author leo-bin
 * @date 2020/7/23 18:00
 * @apiNote 客户端数据传输接口
 */
public interface ClientTransport {

    /**
     * 发送rpc请求
     */
    Object sendRpcObject(RpcRequest rpcRequest);
}
