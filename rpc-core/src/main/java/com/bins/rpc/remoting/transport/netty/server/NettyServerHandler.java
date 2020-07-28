package com.bins.rpc.remoting.transport.netty.server;

import com.bins.rpc.enums.RpcMessageTypeEnum;
import com.bins.rpc.enums.RpcResponseCode;
import com.bins.rpc.factory.SingletonFactory;
import com.bins.rpc.remoting.dto.RpcRequest;
import com.bins.rpc.remoting.dto.RpcResponse;
import com.bins.rpc.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo-bin
 * @date 2020/7/28 0:27
 * @apiNote
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler requestHandler;


    public NettyServerHandler() {
        requestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("服务端收到消息: [{}] ", msg);
            RpcRequest rpcRequest = (RpcRequest) msg;

            //处理心跳检测包
            if (rpcRequest.getRpcMessageTypeEnum() == RpcMessageTypeEnum.HEART_BEAT) {
                log.info("成功收到来自客户端的心跳检测包。。。");
                return;
            }

            //处理实际rpc请求
            Object result = requestHandler.handle(rpcRequest);
            log.info("rpc请求已经处理完毕。正在发送请求结果...");
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCode.FAIL);
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                log.error("not writable now, message dropped");
            }
        } finally {
            //手动管理gc
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
