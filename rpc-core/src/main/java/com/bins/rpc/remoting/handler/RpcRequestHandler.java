package com.bins.rpc.remoting.handler;

import com.bins.rpc.enums.RpcResponseCode;
import com.bins.rpc.exception.RpcException;
import com.bins.rpc.provider.ServiceProvider;
import com.bins.rpc.provider.ServiceProviderImpl;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author leo-bin
 * @date 2020/7/23 21:35
 * @apiNote 处理rpc请求
 */
@Slf4j
public class RpcRequestHandler {

    private ServiceProvider serviceProvider = new ServiceProviderImpl();

    /**
     * 处理rpc请求
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getServiceProvider(rpcRequest.getServiceProperties().getUniqueServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 拿到实例的方法并执行
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            if (null == method) {
                return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
            }
            result = method.invoke(service, rpcRequest.getParameters());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
