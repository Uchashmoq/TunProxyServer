package proxy.tun.server.transmission.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestToClientHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    private ChannelHandlerContext clientCtx;

    public DestToClientHandler (ChannelHandlerContext clientCtx){
        this.clientCtx=clientCtx;
    }
    @Override
    //将从服务器收到的数据从TransmitBootstrap发给客户端，会经过加密
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf =(ByteBuf)msg;
        int n=buf.readableBytes();
        logger.info("client : {} <- destServer : {}   {} bytes",clientCtx.channel().remoteAddress(),ctx.channel().remoteAddress(),n);
        clientCtx.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client : {} <- destServer : {} , destServer disconnect",clientCtx.channel().remoteAddress(),ctx.channel().remoteAddress());
        clientCtx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
    }
}
