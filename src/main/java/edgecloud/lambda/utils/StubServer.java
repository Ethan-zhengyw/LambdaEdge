package edgecloud.lambda.utils;

import edgecloud.lambda.entity.Node;

import java.util.ArrayList;
import java.util.List;

public class StubServer {

    // TODO
    // Replace stub with true node query code
    public static List<Node> queryNodes() {

        List<Node> nodes = new ArrayList<Node>();

        Node node1 = new Node();
        node1.setId(1);
        node1.setDesc("Node 1");
        node1.setStatus(1);

        Node node2 = new Node();
        node2.setId(2);
        node2.setDesc("Node 2");
        node2.setStatus(0);

        nodes.add(node1);
        nodes.add(node2);

        return nodes;
    }

    // TODO
    public static void pushFunctionToNode() {
    }
}
