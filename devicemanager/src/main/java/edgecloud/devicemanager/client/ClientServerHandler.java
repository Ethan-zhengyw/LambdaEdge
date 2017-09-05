package edgecloud.devicemanager.client;

import edgecloud.devicemanager.DeviceManager;
import edgecloud.devicemanager.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientServerHandler extends SimpleChannelInboundHandler<Message.ServiceMessage> {
    private Logger logger = LoggerFactory.getLogger(ClientServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.ServiceMessage msg) {
        logger.info("device manager recived msg: " + msg);
        DeviceManager.processMessage(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
