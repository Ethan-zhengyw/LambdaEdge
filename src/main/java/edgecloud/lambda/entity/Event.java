package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;

    private String eventName;

    private String eventArgs;

    private String eventResult;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventResult() {
        return eventResult;
    }

    public void setEventResult(String result) {
        this.eventResult = result;
    }

	public String toString() {
        return String.format("{\"id\": %d, \"eventName\": %s, \"eventResult\": %s}",
                id, eventName, eventResult);
    }

    public String getEventArgs() {
        return eventArgs;
    }

    public void setEventArgs(String eventArgs) {
        this.eventArgs = eventArgs;
    }
}
