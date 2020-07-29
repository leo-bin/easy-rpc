package com.bins.rpc.registry.zk;

import com.bins.rpc.enums.RpcErrorMessageEnum;
import com.bins.rpc.exception.RpcException;
import com.bins.rpc.loadbalance.LoadBalance;
import com.bins.rpc.loadbalance.FullRandomBalance;
import com.bins.rpc.registry.ServiceDiscovery;
import com.bins.rpc.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author leo-bin
 * @date 2020/7/28 23:17
 * @apiNote 使用zk作服务发现
 */
@Slf4j
public class ZKServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        loadBalance = new FullRandomBalance();
    }

    /**
     * @param serviceName 服务名字
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceAddresses = CuratorUtils.getChildrenNodes(zkClient, serviceName);
        if (serviceAddresses.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, serviceName);
        }

        //开始对服务列表中的地址进行负载均衡
        //url格式是：
        String url = loadBalance.getServiceAddress(serviceAddresses);
        log.info("成功发现目标服务，服务地址是：[{}]", url);
        //解析url拿到具体的主机和端口
        String[] socketAddressArray = url.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
