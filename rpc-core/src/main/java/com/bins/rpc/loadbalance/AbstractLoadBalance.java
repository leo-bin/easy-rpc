package com.bins.rpc.loadbalance;

import java.util.List;

/**
 * @author leo-bin
 * @date 2020/7/29 1:07
 * @apiNote 负载均衡算法的抽象类
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * 提前过滤掉部分情况
     */
    @Override
    public String getServiceAddress(List<String> serviceAddress) {
        if (serviceAddress == null || serviceAddress.size() == 0) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return null;
    }

    protected abstract String doSelect(List<String> serviceAddress);
}
