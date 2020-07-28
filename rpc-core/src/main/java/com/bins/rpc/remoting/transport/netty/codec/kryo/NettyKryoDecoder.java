package com.bins.rpc.remoting.transport.netty.codec.kryo;

import com.bins.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author leo-bin
 * @date 2020/7/28 0:02
 * @apiNote 自定义netty进行网络传输时的解码器
 */
@Slf4j
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;
    private final Class<?> genericClass;
    /**
     * 编码时消息体的头部所占的长度（int型，占4个字节）
     */
    private static final int HEADER_LENGTH = 4;


    /**
     * 自定义消息解码器
     *
     * @param ctx 解码器关联的 ChannelHandlerContext 对象
     * @param in  "入站"数据，也就是 ByteBuf 对象
     * @param out 解码之后的数据对象需要添加到 out 对象里面
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= HEADER_LENGTH) {
            //标记现在读指针的下标，方便后面进行重置
            in.markReaderIndex();

            int dataLength = in.readInt();
            //提前终止不合理情况
            if (dataLength < 0 || in.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }

            //根据读到的数据长度保证读取的实际数据是完整的,不完整就重置并返回，等待新数据的到来
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }

            //走到这里基本上就没问题了，可以开始反序列化了
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            //反序列化
            Object obj = serializer.deserialize(body, genericClass);
            //添加到输出列表中
            out.add(obj);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
