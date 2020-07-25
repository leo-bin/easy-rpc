package com.bins.rpc.proxy;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.proxy.handler.RpcInvocationHandler;
import com.bins.rpc.remoting.transport.ClientTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author leo-bin
 * @date 2020/7/23 18:02
 * @apiNote 代理工厂，根据给定的处理器和目标类生成一个代理类
 */
public class ProxyFactory {

    /**
     * 根据目标类和定制的处理生成一个代理类
     *
     * @param clazz             目标类的class模板对象
     * @param clientTransport   通信方式
     * @param serviceProperties 请求专属属性
     * @param <T>               目标类的类型
     * @return 代理类
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> clazz, ClientTransport clientTransport, RpcServiceProperties serviceProperties) {
        InvocationHandler handler = new RpcInvocationHandler(clientTransport, serviceProperties);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
    }
}
