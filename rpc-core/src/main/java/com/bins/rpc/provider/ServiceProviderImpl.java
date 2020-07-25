package com.bins.rpc.provider;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.enums.RpcErrorMessageEnum;
import com.bins.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leo-bin
 * @date 2020/7/23 21:45
 * @apiNote 服务提供者实现类
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    /**
     * 如何解决一个接口被多个实现类实现的情况？
     * 1.我们可以给每一个接口的不同实现根据具体的功能特征打上标签
     * 2.最后服务的名字就是：接口名字+标签
     */
    private static Map<String, Object> services = new ConcurrentHashMap<>(16);


    @Override
    public <T> void addServiceProvider(T service, Class<T> serviceClass, RpcServiceProperties serviceProperties) {
        //获取服务的唯一name
        String serviceName = serviceProperties.getUniqueServiceName();
        //已经存在了就不用注册了
        if (services.containsKey(serviceName)) {
            return;
        }
        services.put(serviceName, service);
        log.info("Add Service:{} and interface :{}", serviceName, service.getClass().getInterfaces());
    }


    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = services.get(serviceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
