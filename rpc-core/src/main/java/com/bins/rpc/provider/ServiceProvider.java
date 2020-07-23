package com.bins.rpc.provider;

/**
 * @author leo-bin
 * @date 2020/7/23 21:44
 * @apiNote 服务提供者接口
 */
public interface ServiceProvider {

    /**
     * 注册一个服务提供者
     */
    <T> void addServiceProvider(T service, Class<T> serviceClass);

    /**
     * 通过服务名字拿到服务提供者的实例
     */
    Object getServiceProvider(String serviceName);
}
