package com.granado.java.netty.example.pack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boos, worker)
          .channel(NioServerSocketChannel.class)  // 指定主channel
          .option(ChannelOption.SO_BACKLOG, 1024)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.TCP_NODELAY, true)
          //.handler() 该handler 方法是处理主 channel 的，默认会被 netty 添加 ServerBootstrapAcceptor
          // 添加子channel的handler，该handler会在主channel的ServerBootstrapAcceptor中添加到子channel的pipeline中去
          .childHandler(new ChannelInitializer() {
              @Override
              protected void initChannel(Channel ch) throws Exception {
                  ch.pipeline().addLast(new TimeServerHandler());
              }
          })
          .bind(8000);
    }
}
