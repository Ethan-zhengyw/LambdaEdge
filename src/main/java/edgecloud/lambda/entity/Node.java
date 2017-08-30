package edgecloud.lambda.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Node {
    @Id
    @GeneratedValue
    private Integer id;

    private String nodeName;

    public Integer getId() {
        return id;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }

}