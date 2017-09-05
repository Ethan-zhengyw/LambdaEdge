package edgecloud.devicemanager.apiserver;

import edgecloud.devicemanager.DeviceManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import edgecloud.devicemanager.Message;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

public class DeviceManagerHandler extends SimpleChannelInboundHandler<Message.ClientMessage> {
    private Logger logger = Logger.getLogger(DeviceManagerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.ClientMessage msg) throws Exception{
        logger.info("receive client message: " + msg);
        Channel channel = ctx.channel();

        DeviceManager.deviceMessageProcess(msg, channel);

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
