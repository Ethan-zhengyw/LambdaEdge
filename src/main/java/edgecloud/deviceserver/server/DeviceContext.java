package edgecloud.deviceserver.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edgecloud.lambda.entity.EventFunctionMapping;
import edgecloud.lambda.repository.EventFunctionMappingRepository;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;


public class DeviceContext {
    private static final Logger logger = Logger.getLogger(DeviceContext.class);
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //device ; channel
    private static Map<String, Channel> deviceChannelMap = HashBiMap.create();

    //device manger ; channel
    private static Map<String, Channel> deviceManagerChannelMap = HashBiMap.create();
    // online device
    public static Map<String, Device> onlineDevice = new ConcurrentHashMap<>();

    // delay message queue
    public static BlockingQueue<Message.ServiceMessage> queue = new LinkedBlockingDeque<Message.ServiceMessage>();

    // event and event result
    public static BiMap<String, Message.ClientMessage> eventResultMap = HashBiMap.create();



    public static void online(Message.ClientMessage message, Channel channel) {
        String deviceId = message.getDeviceId();
        logger.info("device is online: " + deviceId);

        channels.add(channel);
        if(message.getType() == 5){
            deviceManagerChannelMap.put(message.getDeviceId(), channel);
        } else {
            deviceChannelMap.put(deviceId, channel);
        }
        Device device = new Device();
        device.setId(deviceId);
        device.setDeviceDesc(message.getContent());
        device.setLastHeartTime(LocalDateTime.now());

        onlineDevice.put(deviceId, device);

        // TODO: when the device online, search the message own of this device, then send it.

    }

    public static void offline(String deviceId, Channel channel) {
        logger.info("device offline: " + deviceId);

        channels.remove(channel);
        deviceChannelMap.remove(deviceId, channel);
        onlineDevice.remove(deviceId);
    }

    public static void ping(String deviceId){
        logger.info("device ping: " + deviceId);
        Device device = onlineDevice.get(deviceId);
        if (device != null) {
            device.setLastHeartTime(LocalDateTime.now());
            onlineDevice.put(deviceId, device);
            return;
        }
        logger.error("device: " + deviceId + " need register firstly");
    }

    public static void addMessage2Queue(Message.ServiceMessage msg){
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void sendMessage(Message.ServiceMessage msg) {
        String deviceId = msg.getDeviceId();
        Channel channel = deviceChannelMap.get(deviceId);
        if (channel != null) {
            if (isOnline(deviceId)) {
                logger.info("send message: " + msg);
                channel.writeAndFlush(msg);
            } else {
                // TODO: need save the message and send it later
                logger.error("device is offline: " + deviceId);
            }
        } else {
            logger.error("send message error");
        }
    }

    public static boolean isOnline(String deviceId){
        return onlineDevice.containsKey(deviceId);
    }

    //sync event need result
    public static void postResult(Message.ClientMessage msg) {

        String callbackId = msg.getCallbackId();
        logger.info(callbackId + " get result.");
        Message.ClientMessage message = eventResultMap.get(callbackId);
        if (message == null) {
            eventResultMap.put(callbackId, msg);
        } else{
            logger.error(callbackId + " result is existc.");
        }
    }

    public static void sendEventFunctionMap(String content) {
        logger.info("deviceManagerChannelMap: " + deviceManagerChannelMap);
        for (Channel channel : deviceManagerChannelMap.values()) {
            Message.ServiceMessage msg = Message.ServiceMessage.newBuilder()
                    .setContent(content)
                    .setType(3)
                    .setDeviceId("NULL")
                    .setMessageId("NULL")
                    .build();
            System.out.println(msg);
            channel.writeAndFlush(msg);
        }

    }
}

