package com.bins.rpc.registry.staticservice;

import com.bins.rpc.registry.ServiceDiscovery;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author leo-bin
 * @date 2020/7/23 19:03
 * @apiNote 静态的ip地址（先写死）
 */
public class StaticServiceDiscovery implements ServiceDiscovery {

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        String host = "127.0.0.1";
        int port = 9000;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return new InetSocketAddress(host, port);
    }
}
