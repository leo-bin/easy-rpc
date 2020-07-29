package com.bins.rpc.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leo-bin
 * @date 2020/7/29 20:50
 * @apiNote 完全轮询随机算法
 */
public final class FullRoundBalance extends AbstractLoadBalance {

    /**
     * askCount记录每一次请求服务器地址的索引，每请求一次count+1
     */
    private static AtomicInteger askCount = new AtomicInteger(0);


    @Override
    protected String doSelect(List<String> serviceAddress) {
        if (askCount.get() == serviceAddress.size()) {
            askCount.set(0);
        }
        return serviceAddress.get(askCount.getAndIncrement());
    }
}
