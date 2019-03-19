package com.granado.java.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoDataHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOG = LoggerFactory.getLogger(EchoDataHandler.class);
    /**
     * 处理业务逻辑
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf data = (ByteBuf) msg;
            //拷贝一份数据
            ByteBuf readCopy = data.copy();
            int len =  readCopy.readableBytes();
            byte[] arr = new byte[len];
            readCopy.getBytes(0, arr);
            LOG.info("read data:\n {}", new String(arr));
            ctx.write(arr);
            ctx.flush();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //读完后flush空数据
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常
        LOG.error("exception happened from indound: {}", cause.getMessage());
        ctx.close();
    }
}
