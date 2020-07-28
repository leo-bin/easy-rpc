package com.bins.rpc.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @author leo-bin
 * @date 2020/7/29 1:03
 * @apiNote 使用完全随机实现负载均衡算法
 */
public class RandomBalance extends AbstractLoadBalance {


    /**
     * 随机返回0-size内的一个地址
     */
    @Override
    protected String doSelect(List<String> serviceAddress) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
