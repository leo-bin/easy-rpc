package com.bins.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author leo-bin
 * @date 2020/7/23 19:00
 * @apiNote 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param serviceName   服务名字
     * @param socketAddress 服务地址
     */
    void registryService(String serviceName, InetSocketAddress socketAddress);
}
