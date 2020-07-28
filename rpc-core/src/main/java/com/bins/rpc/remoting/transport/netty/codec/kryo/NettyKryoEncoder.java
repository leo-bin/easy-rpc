package com.bins.rpc.remoting.transport.netty.codec.kryo;

import com.bins.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @author leo-bin
 * @date 2020/7/28 0:02
 * @apiNote 基于netty中的编码器结合自定义序列化机制kryo实现传输数据的编码
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;
    private final Class<?> genericClass;


    /**
     * 自定义编码格式：
     * 1.将对象序列化，然后写入byteBuf变成二进制流
     * 2.这里使用自定义消息的结构来解决粘包和拆包的问题
     * 3.经过编码之后的二进制数据的结构就是：dataLength:data
     * 4.通过设置数据的实际长度，在解码时先读实际数据长度，然后依次读length长度的数据，以此来拆包
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        if (genericClass.isInstance(msg)) {
            byte[] body = serializer.serialize(msg);
            int header = body.length;
            //分别设置请求头和请求体
            out.writeInt(header);
            out.writeBytes(body);
        }
    }
}
