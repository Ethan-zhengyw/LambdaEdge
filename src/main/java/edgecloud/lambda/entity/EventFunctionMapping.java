package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
public class EventFunctionMapping {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer funcId;

    private String funcName;

    private Integer eventId;

    private String eventName;

    public Integer getId() {
        return id;
    }

    public void setFuncId(Integer funcId) {
        this.funcId = funcId;
    }

    public Integer getFuncId() {
        return funcId;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public String toString() {
        return String.format("{\"id\": \"%d\", \"funcName\": \"%s\", \"eventName\": \"%s\"}",
                id, funcName, eventName);
    }
}
