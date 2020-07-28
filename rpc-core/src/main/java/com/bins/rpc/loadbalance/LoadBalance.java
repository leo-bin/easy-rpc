package com.bins.rpc.loadbalance;

import java.util.List;

/**
 * @author leo-bin
 * @date 2020/7/29 1:03
 * @apiNote 负载均衡算法
 */
public interface LoadBalance {

    /**
     * 从服务地址中选取一个可用的地址
     */
    String getServiceAddress(List<String> serviceAddress);
}
