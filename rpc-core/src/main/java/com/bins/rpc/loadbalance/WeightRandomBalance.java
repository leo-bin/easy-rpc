package com.bins.rpc.loadbalance;

import java.util.List;

/**
 * @author leo-bin
 * @date 2020/7/29 20:47
 * @apiNote 加权哈希算法
 */
public class WeightRandomBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddress) {

        return null;
    }
}
