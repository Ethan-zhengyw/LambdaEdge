package edgecloud.devicemanager;

import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edgecloud.devicemanager.apiserver.Device;
import edgecloud.devicemanager.apiserver.HttpServer;
import edgecloud.devicemanager.client.ClientServer;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceManager {
    public static String restapiHost;
    public static Map<String, String>  eventFunctionMap = new ConcurrentHashMap<>();
    public static Map<String, Device> onlineDevice = new ConcurrentHashMap<>();

    //device ; channel
    private static Map<String, Channel> deviceChannelMap = HashBiMap.create();
    private static Map<String, String> eventResultMap = new HashMap<>();
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void processMessage(Message.ServiceMessage msg){
        System.out.println(msg);
        if(msg.getType() == 3){
            // 3: event function map
            String content = msg.getContent();
            JSONObject jo = new JSONObject(content);
            String funcName = jo.getString("funcName");
            String eventName = jo.getString("eventName");
            int version = jo.getInt("version");
            eventFunctionMap.put(eventName, funcName+":"+version);
        }else{
            System.out.println("server push wrong message to me.");
        }
    }

    public static String sendMessageToClient(int type, String content) {
        String msgId = UUID.randomUUID().toString();
        System.out.println(deviceChannelMap);

        String[] keys = deviceChannelMap.keySet().toArray(new String[0]);
        Random random = new Random();
        String deviceId = keys[random.nextInt(keys.length)];

        Message.ServiceMessage serverMessage = Message.ServiceMessage.newBuilder()
                .setMessageId(msgId)
                .setDeviceId(deviceId)
                .setType(type)
                .setContent(content)
                .build();
        System.out.println("send message:\n "+ serverMessage);
        deviceChannelMap.get(deviceId).writeAndFlush(serverMessage);
        String result =  "Get message result failed";
        String tmp = null;
        int tryCount = 10;
        if (type == 0 || type == 1) {
            result = msgId;
        } else if (type == 2){
            while(tryCount > 0) {
                tmp = eventResultMap.get(msgId);
                if (tmp == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tryCount--;
                } else {
                    result = tmp;
                    eventResultMap.remove(msgId);
                    break;
                }
            }

        }
        return result;


    }

    public static boolean isOnline(String deviceId){
        return onlineDevice.containsKey(deviceId);
    }


    public static void deviceMessageProcess(Message.ClientMessage message, Channel channel) {
        String deviceId = message.getDeviceId();
//      logger.info("device is online: " + deviceId);
        if (message.getType() == 1) {
            channels.add(channel);
            deviceChannelMap.put(deviceId, channel);
            Device device = new Device();
            device.setId(deviceId);
            device.setDeviceDesc(message.getContent());
            device.setLastHeartTime(LocalDateTime.now());

            onlineDevice.put(deviceId, device);
        } else if (message.getType() == 4){
            eventResultMap.put(message.getCallbackId(), message.getContent());
            System.out.println(eventResultMap);
        }

    }


    public static void getEventFunctionMap() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://" + restapiHost +"/list_event_func_mapping");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        JSONArray ja = new JSONArray(result);
        int len = ja.length();
        for (int i = 0; i < len; i++) {
            JSONObject jo = ja.getJSONObject(i);
            String funcName = jo.getString("funcName");
            String eventName = jo.getString("eventName");
            eventFunctionMap.put(eventName, funcName);
        }
        System.out.println(eventFunctionMap);


    }

    public static void main(String []args) throws Exception{

        // start http server

        HttpServer httpServer = new HttpServer(5000);
        new Thread(httpServer).start();
        restapiHost = "162.3.200.191:8081";
        getEventFunctionMap();
        ClientServer clientServer = new ClientServer("162.3.200.191", 8082);
        clientServer.run();
    }

}
