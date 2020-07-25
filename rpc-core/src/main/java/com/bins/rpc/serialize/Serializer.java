package com.bins.rpc.serialize;

/**
 * @author leo-bin
 * @date 2020/7/26 0:57
 * @apiNote 自定义序列化接口
 */
public interface Serializer {

    /**
     * 序列化：对象->字节
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化：字节->具体对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
