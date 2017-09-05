package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class EventResult {

    @Id
    @GeneratedValue
    private Integer id;

    private String finishTime;

    private Integer eventId;
    private String eventName;

    private Integer funcId;
    private String funcName;

    private Integer nodeId;
    private String nodeDesc;

    private String eventResult;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFinishTime() {
        return finishTime;
    }
    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getEventId() {
        return eventId;
    }
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getFuncId() {
        return funcId;
    }
    public void setFuncId(Integer funcId) {
        this.funcId = funcId;
    }

    public String getFuncName() {
        return funcName;
    }
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Integer getNodeId() {
        return nodeId;
    }
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }
    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getEventResult() {
        return eventResult;
    }
    public void setEventResult(String result) {
        this.eventResult = result;
    }

	public String toString() {
        return String.format("{\"id\": %d, \"eventName\": %s, \"funcId\": %d, \"nodeId\": %d, \"eventResult\": %s}",
                id, eventName, funcId, nodeId, eventResult);
    }
}
