package proxy.tun.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class DetectIdleHandler extends ChannelDuplexHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final DetectIdleHandler instance=new DetectIdleHandler();
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state()== IdleState.ALL_IDLE){
                logger.info("{} idle",ctx.channel().toString());
                ctx.channel().close();
            }
        }
    }
}
