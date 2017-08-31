package edgecloud.lambda.utils;

import edgecloud.deviceserver.ServerAPI;
import edgecloud.deviceserver.server.Device;
import edgecloud.lambda.entity.Function;
import edgecloud.lambda.entity.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StubServer {

    private static Logger log = LoggerFactory.getLogger(StubServer.class);
    private static ServerAPI serverAPI = new ServerAPI();

    // TODO
    // Replace stub with true node query code
    public static List<Node> queryNodes() {
        log.info("Calling ServerAPI to get all devices...");
        List<Device> devices = serverAPI.getAllDevice();
        log.info("Result devices length: " + devices.size());

        List<Node> nodes = new ArrayList<Node>();
        for (Device device : devices) {

            log.info("Device: " + device.toString());

            Node node = new Node();
            node.setId(Integer.valueOf(device.getId()));
            node.setDesc(device.getDeviceDesc());
            nodes.add(node);
        }

//        Node node1 = new Node();
//        node1.setId(1);
//        node1.setDesc("Node 1");
//        node1.setStatus(1);
//
//        Node node2 = new Node();
//        node2.setId(2);
//        node2.setDesc("Node 2");
//        node2.setStatus(0);
//
//        nodes.add(node1);
//        nodes.add(node2);

        return nodes;
    }

    // TODO
    public static void pushFunctionToNode(Integer nodeId, Function function) throws InterruptedException {

        String deviceId = String.valueOf(nodeId);
        int type = 0;
        String content = function.toString();

        serverAPI.sendMessage(type, deviceId, content);

    }
}
