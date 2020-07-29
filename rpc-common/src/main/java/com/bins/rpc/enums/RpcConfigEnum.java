package com.bins.rpc.enums;

/**
 * @author leo-bin
 * @date 2020/7/28 23:29
 * @apiNote 一些rpc相关的配置属性
 */
public enum RpcConfigEnum {

    /**
     * rpc配置文件所在路径
     */
    RPC_CONFIG_PATH("rpc.properties"),

    /**
     * netty绑定的端口
     */
    RPC_NETTY_PORT("rpc.netty.port"),

    /**
     * 负载均衡算法类型
     */
    RPC_LOAD_BALANCE_TYPE("rpc.loadBalance.type"),

    /**
     * zk默认地址属性key
     */
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String configValue;

    RpcConfigEnum(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigValue() {
        return configValue;
    }
}
