package com.bins.rpc.enums;


/**
 * @author leo-bin
 * @date 2020/7/29 21:05
 * @apiNote 负载均衡算法类型枚举类
 */
public enum LoadBalanceTypeEnum {

    /**
     * 完全随机
     */
    FULL_RANDOM(1),

    /**
     * 完全轮询
     */
    FULL_ROUND(2),

    /**
     * 加权随机
     */
    Weight_RANDOM(3);


    private int type;

    LoadBalanceTypeEnum(int type) {
        this.type = type;
    }

    public final int getType() {
        return type;
    }
}
