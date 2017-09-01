package edgecloud.lambda.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Node {

    @Id
    @GeneratedValue
    private Integer id;

    private String desc;

    private Integer status;
    // 1 - online
    // 0 - offline



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("{\"id\": \"%d\", \"desc\", \"%s\"}", id, desc);
    }
}