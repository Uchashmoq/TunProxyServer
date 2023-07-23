package proxy.tun.server.transmission;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import proxy.tun.server.protocol.TunMessageCodec;
import proxy.tun.server.transmission.handler.DestToClientHandler;
import proxy.tun.tool.AesCodec;

public class TransmitBootstrap extends Bootstrap {
    private static  NioEventLoopGroup worker = new NioEventLoopGroup();
    private ChannelHandlerContext clientCtx;


    public TransmitBootstrap(ChannelHandlerContext ctx){
        this.clientCtx=ctx;
        group(worker);
        channel(NioSocketChannel.class);
        option(ChannelOption.TCP_NODELAY,true);
        handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline()
                        //监听dst，从TunServerBootstrap发回
                        .addLast(new DestToClientHandler(clientCtx));
            }
        });

    }

    public Channel syncConnect(String dstAddr,int port) throws InterruptedException {
        ChannelFuture channelFuture = connect(dstAddr, port);
        channelFuture.sync();
        return channelFuture.channel();
    }
}
