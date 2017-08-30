package edgecloud.deviceserver.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Logger;

public class DeviceServerHandler extends SimpleChannelInboundHandler<Message.ClientMessage>{
    private Logger logger = Logger.getLogger(DeviceServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.ClientMessage msg) throws Exception{
        logger.info("receive client message: " + msg);
        Channel channel = ctx.channel();
        channel.writeAndFlush(msg);
        new ClientMessageRouter(channel, msg).router();

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
