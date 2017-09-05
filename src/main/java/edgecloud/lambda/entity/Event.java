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

	public String toString() {
        return String.format("{\"id\": %d, \"eventName\": %s}",  id, eventName);
    }

    public String getEventArgs() {
        return eventArgs;
    }

    public void setEventArgs(String eventArgs) {
        this.eventArgs = eventArgs;
    }
}
