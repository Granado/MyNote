package com.granado.java.netty.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Author: Granado
 * @Date: 2019-05-07 14:02
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private volatile int counter;

    private static final AtomicIntegerFieldUpdater counterUpdater = AtomicIntegerFieldUpdater.newUpdater(TimeClientHandler.class,
      "counter");

    private byte[] req = "QUERY TIME ORDER".getBytes();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF8");
        System.out.println("Now is: " + body + ", counter is: " + counterUpdater.addAndGet(this, 1));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
