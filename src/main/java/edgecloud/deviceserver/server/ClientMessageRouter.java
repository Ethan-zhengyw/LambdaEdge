package edgecloud.deviceserver.server;

import edgecloud.common.Constants;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMessageRouter {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private Channel channel;
    private Message.ClientMessage message;

    public ClientMessageRouter(Channel channel, Message.ClientMessage message) {
        this.channel = channel;
        this.message = message;
    }

    public void router(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                switch (message.getType()){
                    case Constants.CLIENT_MESSAGE_ON:
                        DeviceContext.online(message, channel);
                        break;
                    case Constants.CLIENT_MESSAGE_OFF:
                        DeviceContext.offline(message.getDeviceId(), channel);
                        break;
                    case Constants.CLIENT_MESSAGE_PING:
                        DeviceContext.ping(message.getDeviceId());
                        break;
                    case Constants.CLIENT_MESSAGE_RESULT:
                        DeviceContext.postResult(message);
                        break;
                    case Constants.CLIENT_MESSAGE_OTHER:
                        break;
                    default:
                        throw new IllegalArgumentException("this message type is invalid");
                }
            }
        });
    }

}
