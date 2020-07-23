package com.bins.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author leo-bin
 * @date 2020/7/23 19:00
 * @apiNote 服务发现接口
 */
public interface ServiceDiscovery {

    /**
     * 查找服务地址
     *
     * @param serviceName 服务名字
     * @return 服务地址
     */
    InetSocketAddress lookupService(String serviceName);
}
