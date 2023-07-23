package proxy.tun.server.transmission.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientToDestHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger =LoggerFactory.getLogger(this.getClass());
    private Channel dstServerChannel;

    public ClientToDestHandler(Channel dstServerChannel){
        this.dstServerChannel=dstServerChannel;
    }

    @Override
    //接收客户端数据，从dstServerChannel发出，会经过TransmitBootstrap的handlers,但不会加密
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf =(ByteBuf)msg;
        int n=buf.readableBytes();
        logger.info("client : {} -> destServer : {}  {} bytes",ctx.channel().remoteAddress(),dstServerChannel.remoteAddress(),n);
        dstServerChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client : {} -> destServer : {} client disconnect",ctx.channel().remoteAddress(),dstServerChannel.remoteAddress());
        dstServerChannel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
    }
}
