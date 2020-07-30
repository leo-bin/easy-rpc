package com.bins.rpc.registry.zk;

import com.bins.rpc.enums.LoadBalanceTypeEnum;
import com.bins.rpc.enums.RpcConfigEnum;
import com.bins.rpc.enums.RpcErrorMessageEnum;
import com.bins.rpc.exception.RpcException;
import com.bins.rpc.factory.SingletonFactory;
import com.bins.rpc.loadbalance.FullRoundBalance;
import com.bins.rpc.loadbalance.LoadBalance;
import com.bins.rpc.loadbalance.FullRandomBalance;
import com.bins.rpc.loadbalance.WeightRandomBalance;
import com.bins.rpc.registry.ServiceDiscovery;
import com.bins.rpc.registry.zk.util.CuratorUtils;
import com.bins.rpc.utils.PropertiesFileUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

/**
 * @author leo-bin
 * @date 2020/7/28 23:17
 * @apiNote 使用zk作服务发现
 */
@Slf4j
@Builder
@AllArgsConstructor
public class ZKServiceDiscovery implements ServiceDiscovery {

    /**
     * 负载均衡算法
     */
    private LoadBalance loadBalance;
    /**
     * 默认的负载均衡算法：完全随机
     */
    private final LoadBalance defaultBalance;

    public ZKServiceDiscovery() {
        defaultBalance = SingletonFactory.getInstance(FullRandomBalance.class);
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
        //url格式是：/easy-rpc/com.bins.rpc.Calculator-standard/192.168.43.228:9000

        // TODO: BY leo-bin 2020/7/29
        // TODO-LIST: 可以设置一个全局的负载均衡配置类而不是反复读取配置文件

        String url;
        if (loadBalance != null) {
            url = loadBalance.getServiceAddress(serviceAddresses);
        } else {
            url = defaultBalance.getServiceAddress(serviceAddresses);
        }

        log.info("成功发现目标服务，服务地址是：[{}]", url);
        //解析url拿到具体的主机和端口
        String[] socketAddressArray = url.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }


    /**
     * 选择负载均衡算法
     */
    public void setLoadBalance(int type) {
        if (type == LoadBalanceTypeEnum.FULL_RANDOM.getType()) {
            this.loadBalance = SingletonFactory.getInstance(FullRandomBalance.class);
        }
        if (type == LoadBalanceTypeEnum.FULL_ROUND.getType()) {
            this.loadBalance = SingletonFactory.getInstance(FullRoundBalance.class);
        }
        if (type == LoadBalanceTypeEnum.Weight_RANDOM.getType()) {
            this.loadBalance = SingletonFactory.getInstance(WeightRandomBalance.class);
        }
    }
}
