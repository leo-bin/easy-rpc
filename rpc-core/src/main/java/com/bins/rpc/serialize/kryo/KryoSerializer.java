package com.bins.rpc.serialize.kryo;

import com.bins.rpc.exception.SerializeException;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author leo-bin
 * @date 2020/7/26 1:03
 * @apiNote 使用kryo作为序列化工具
 */
@Slf4j
public class KryoSerializer implements Serializer {

    /**
     * kryo线程不安全，使用ThreadLocal实现线程之间的隔离
     */
    private final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Output output = new Output(outputStream)) {
            Kryo kryo = kryos.get();
            kryo.writeObject(output, obj);
            kryos.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialize failed");
        }
    }


    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(inputStream)) {
            Kryo kryo = kryos.get();
            Object o = kryo.readObject(input, clazz);
            kryos.remove();
            return clazz.cast(o);
        } catch (Exception e) {
            throw new SerializeException("deserialize failed");
        }
    }
}
