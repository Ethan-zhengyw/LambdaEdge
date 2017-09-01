package edgecloud.deviceserver;

import edgecloud.deviceserver.server.Device;
import edgecloud.deviceserver.server.DeviceContext;
import edgecloud.deviceserver.server.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerAPI {
    private static Logger logger = Logger.getLogger(ServerAPI.class);

    /**
     *  sync event need wait the message result.
     * @param type send funciton: 0, send sync event: 1, send async event: 2
     * @param deviceId
     * @param content
     * @return
     */
    public String sendMessage(int type, String deviceId, String content) throws InterruptedException {
        String msgId = UUID.randomUUID().toString();
        if (type == 0 || type == 1) {
            Message.ServiceMessage serverMessage = Message.ServiceMessage.newBuilder()
                    .setMessageId(msgId)
                    .setDeviceId(deviceId)
                    .setType(type)
                    .setContent(content)
                    .build();
            DeviceContext.sendMessage(serverMessage);
            return msgId;
        } else if (type == 2) {
            Message.ServiceMessage serviceMessage = Message.ServiceMessage.newBuilder()
                    .setMessageId(msgId)
                    .setDeviceId(deviceId)
                    .setType(type)
                    .setContent(content)
                    .build();
            DeviceContext.sendMessage(serviceMessage);

            // FIXME: ???
            int tryCount = 10;
            String result = "get message result failed";
            while (tryCount > 0) {
                Message.ClientMessage message = DeviceContext.eventResultMap.get(msgId);
                if (message != null) {
                    result = message.getContent();
                    return result;
                }
                tryCount--;
                Thread.sleep(500);
            }
            return result;
        } else {
            return "event type is invalid";
        }
    }

    // TODO: filter
    public List<Device> getAllDevice(){
        List<Device> devices = new ArrayList<Device>();
        for(Device device: DeviceContext.onlineDevice.values()){
            devices.add(device);
        }
        return devices;
    }

}
