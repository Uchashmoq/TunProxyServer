package proxy.tun.server.protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.tun.server.AttributeKeys;
import proxy.tun.server.TunServerBootstrap;
import proxy.tun.tool.AesCodec;
import java.nio.charset.StandardCharsets;
import java.util.List;
/*
*0x89 0x64 dataLen(4 bytes) data
*
 */
@ChannelHandler.Sharable
public class TunMessageCodec extends MessageToMessageCodec<ByteBuf,ByteBuf> {
    public static final TunMessageCodec instance=new TunMessageCodec();
    public static final byte[] MAGIC_NUM={(byte) 0x89,0x64};
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] data=new byte[msg.readableBytes()];
        msg.readBytes(data);
        byte[] encrypt = AesCodec.encrypt(data, ctx.channel().attr(AttributeKeys.KEY).get(), TunServerBootstrap.iv);
        ByteBuf buffer = ctx.alloc().buffer(2 + 4 + encrypt.length);
        buffer.writeBytes(MAGIC_NUM);
        buffer.writeInt(encrypt.length);
        buffer.writeBytes(encrypt);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte b0=msg.readByte(),b1=msg.readByte();
        int len=msg.readInt();
        byte[] data = new byte[len];
        msg.readBytes(data);
        if (b0==MAGIC_NUM[0]&&b1==MAGIC_NUM[1]){
            byte[] key = ctx.channel().attr(AttributeKeys.KEY).get();
            byte[] decrypt = AesCodec.decrypt(data, key, TunServerBootstrap.iv);
            ByteBuf buffer = ctx.alloc().buffer(decrypt.length);
            buffer.writeBytes(decrypt);
            out.add(buffer);
        }else {
            logger.warn("invalid data : {}", new String(data, StandardCharsets.UTF_8));
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}",cause.toString());
    }
}
