package com.granado.java.netty.example.pack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();

        NioEventLoopGroup boos = new NioEventLoopGroup();
        bootstrap.group(boos)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer(){
              @Override
              protected void initChannel(Channel ch) throws Exception {
                  ch.pipeline().addLast(new TimeClientHandler());
              }
          })
          .connect("127.0.0.1", 8000);
    }
}
