package proxy.tun.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.tun.server.transmission.TransmitBootstrap;
import proxy.tun.server.transmission.handler.ClientToDestHandler;

public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) throws InterruptedException {
        if(msg.decoderResult().isFailure()){
            ctx.fireChannelRead(msg);
            return;
        }
        if(!msg.version().equals(SocksVersion.SOCKS5)) {
            logger.debug(String.format("version wrong : %s , addr :%s", msg.version(),ctx.channel().remoteAddress().toString()));
            return;
        }
        Socks5CommandType type = msg.type();
        Socks5AddressType socks5AddressType = msg.dstAddrType();
        String addr = msg.dstAddr();
        int port = msg.dstPort();
        logger.debug(String.format("cmd : %s, dstAddrType : %s , dstPort : %d",type,socks5AddressType,port));

        if(type.equals(Socks5CommandType.CONNECT)){
            TransmitBootstrap transmitBootstrap = new TransmitBootstrap(ctx);
            Channel dstServerChannel = transmitBootstrap.syncConnect(addr, port);
            //监听从TunServerBootstrap收到的数据，通过dstServerChannel发出
            ClientToDestHandler clientToDestHandler = new ClientToDestHandler(dstServerChannel);
            ctx.pipeline().addLast("clientToDestHandler",clientToDestHandler);
            ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS,Socks5AddressType.IPv4));
            logger.trace(String.format("connect %s successfully",dstServerChannel.remoteAddress()));
        }else {
            ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.COMMAND_UNSUPPORTED,Socks5AddressType.IPv4));
            ctx.fireChannelRead(msg);
        }
    }

    //fail to connect dstServer
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       if (cause instanceof InterruptedException){
           ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE,Socks5AddressType.IPv4));
       }else {
           ctx.pipeline().remove("clientToDestHandler");
       }
       super.exceptionCaught(ctx,cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().remove("clientToDestHandler");
    }
}
