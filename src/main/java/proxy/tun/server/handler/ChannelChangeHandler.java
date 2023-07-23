package proxy.tun.server.handler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.tun.server.AttributeKeys;
import proxy.tun.tool.AesCodec;
import proxy.tun.tool.RandomStringGenerator;

import java.nio.charset.StandardCharsets;
@ChannelHandler.Sharable
public class ChannelChangeHandler extends ChannelInboundHandlerAdapter {
    public static final ChannelChangeHandler instance=new ChannelChangeHandler();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        byte[] key  = RandomStringGenerator.generateRandomString(AesCodec.KeyLen).getBytes(StandardCharsets.UTF_8);
        channel.attr(AttributeKeys.KEY).set(key);
        ctx.writeAndFlush(Unpooled.wrappedBuffer(key));
        logger.info("{} connected",channel.remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("{} disconnected",channel.remoteAddress().toString());
    }
}
