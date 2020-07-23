package com.bins.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author leo-bin
 * @date 2020/7/23 19:03
 * @apiNote 静态的ip地址（先写死）
 */
public class StaticServiceDiscovery implements ServiceDiscovery {

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        return new InetSocketAddress("127.0.0.1", 9000);
    }
}
