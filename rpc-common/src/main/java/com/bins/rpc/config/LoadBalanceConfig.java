package com.bins.rpc.config;

import com.bins.rpc.enums.LoadBalanceTypeEnum;

/**
 * @author leo-bin
 * @date 2020/7/29 21:53
 * @apiNote
 */
public class LoadBalanceConfig {

    public static final LoadBalanceTypeEnum defaultBalanceType = LoadBalanceTypeEnum.FULL_RANDOM;
}
