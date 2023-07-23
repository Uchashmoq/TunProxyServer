package proxy.tun.server;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import proxy.tun.server.handler.ChannelChangeHandler;
import proxy.tun.server.handler.DetectIdleHandler;
import proxy.tun.server.handler.Socks5CommandRequestHandler;
import proxy.tun.server.handler.Socks5InitialRequestHandler;
import proxy.tun.server.protocol.TunMessageCodec;

import java.nio.ByteOrder;
public class TunServerChannelInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(ChannelChangeHandler.instance)
                .addLast(new IdleStateHandler(0,0,3600))
                .addLast(DetectIdleHandler.instance)
                .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN,1024*1024*5,2,4,0,0,true))
                .addLast(TunMessageCodec.instance)
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(Socks5ServerEncoder.DEFAULT)
                .addLast(new Socks5InitialRequestDecoder())
                .addLast(Socks5InitialRequestHandler.instance)
                .addLast(new Socks5CommandRequestDecoder())
                .addLast(new Socks5CommandRequestHandler());
    }
}
