package com.granado.java.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

public class EchoServerChannelHandler extends ChannelInitializer<SocketChannel> {

    private Set<SocketChannel> socketChannels = new ConcurrentSkipListSet<>();

    private AtomicLong socketCounter = new AtomicLong(0);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        if (!socketChannels.contains(ch) && socketChannels.add(ch)) {
            socketCounter.incrementAndGet();
        }

        ch.pipeline().addLast(new EchoDataHandler());
    }
}
