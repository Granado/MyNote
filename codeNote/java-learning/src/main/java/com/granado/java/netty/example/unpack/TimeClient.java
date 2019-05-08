package com.granado.java.netty.example.unpack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();

        NioEventLoopGroup boos = new NioEventLoopGroup();
        bootstrap.group(boos)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer(){
              @Override
              protected void initChannel(Channel ch) throws Exception {
                  ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                  ch.pipeline().addLast(new StringDecoder());
                  ch.pipeline().addLast(new TimeClientHandler());
              }
          })
          .connect("127.0.0.1", 8000);
    }
}
