package edgecloud.devicemanager.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edgecloud.devicemanager.Message;

import java.util.UUID;

public class ClientServer {

    private static Logger logger = LoggerFactory.getLogger(ClientServer.class);

    private  int port;
    private  String host;
    private  ChannelFuture future = null;
    public ClientServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public  void run() throws Exception {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(
                            new ProtobufDecoder(Message.ServiceMessage.getDefaultInstance()));
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new ProtobufEncoder());
                    ch.pipeline().addLast(new ClientServerHandler());
                }
            });
            future = bootstrap.connect(host, port).sync();
            // register device manager
            sendMessage(5, "DeviceManager1", "NULL");

            future.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    public String sendMessage(int type, String deviceId, String content) {
        String msgId = UUID.randomUUID().toString();
        Message.ClientMessage clientMessage = Message.ClientMessage.newBuilder()
                    .setMessageId(msgId)
                    .setDeviceId(deviceId)
                    .setType(type)
                    .setContent(content)
                    .build();
        future.channel().writeAndFlush(clientMessage);
        return msgId;

    }
//
//    public static void main(String [] args) throws Exception {
//        new ClientServer("xxx", 8082).run();
//    }


}
