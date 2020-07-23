package com.bins.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leo-bin
 * @date 2020/7/23 21:37
 * @apiNote 单例对象工厂
 */
public final class SingletonFactory {
    private static Map<String, Object> singletonObjects = new HashMap<>(16);

    private SingletonFactory() {
    }


    /**
     * 对象如果存在那就创建，不存在就返回map中的对象
     */
    public static <T> T getInstance(Class<T> clazz) {
        String key = clazz.toString();
        Object instance = singletonObjects.get(key);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    singletonObjects.put(key, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }
}
