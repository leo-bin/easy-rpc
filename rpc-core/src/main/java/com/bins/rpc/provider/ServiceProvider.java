package com.bins.rpc.provider;

import com.bins.rpc.entity.RpcServiceProperties;

/**
 * @author leo-bin
 * @date 2020/7/23 21:44
 * @apiNote 服务提供者接口
 */
public interface ServiceProvider {

    /**
     * 发布服务并初始化服务
     */
    <T> void publishService(T service, Class<T> serviceClass,RpcServiceProperties serviceProperties);

    /**
     * 通过服务名字拿到服务提供者的实例
     */
    Object getService(String serviceName);
}
