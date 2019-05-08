package com.granado.java.netty.example.pack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Author: Granado
 * @Date: 2019-05-07 10:42
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    public static final String LINE_SEPARATOR = "\r\n";

    private static final Charset UTF8 = Charset.forName("utf8");

    private volatile int counter;

    private AtomicIntegerFieldUpdater counterUpdater = AtomicIntegerFieldUpdater.newUpdater(this.getClass(), "counter");

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, UTF8).substring(0, req.length - LINE_SEPARATOR.length());

        print(body);

        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? LocalDateTime.now().toString() : "BAD ORDER";
        currentTime += LINE_SEPARATOR;

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        context.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    private void print(String body) {
        System.out.println("The time server receive order  : " + body + " ; the counter is : " + (counterUpdater.addAndGet(this, 1)));
    }
}
