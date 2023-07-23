package proxy.tun.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        int n = buf.readableBytes();
        for (int i = 0; i < n; i++) {
            System.out.printf("%x", buf.getByte(i));
        }
        ctx.fireChannelRead(msg);
    }
}
