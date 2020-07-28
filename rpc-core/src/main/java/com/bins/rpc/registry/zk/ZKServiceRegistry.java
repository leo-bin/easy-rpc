package com.bins.rpc.registry.zk;

import com.bins.rpc.registry.ServiceRegistry;
import com.bins.rpc.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @author leo-bin
 * @date 2020/7/28 23:16
 * @apiNote 使用zk作为服务注册中心
 */
@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {

    /**
     * 往zk中注册一个服务（实际上就是根据服务名和地址创建了一个节点）
     *
     * @param serviceName   服务名字
     * @param socketAddress 服务地址
     */
    @Override
    public void registryService(String serviceName, InetSocketAddress socketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTRY_ROOT_PATH + "/" + serviceName + socketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
