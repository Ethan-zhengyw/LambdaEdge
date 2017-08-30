package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;

//    private Integer eventId;

    private String eventName;

    public Integer getId() {
        return id;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public String toString() {
        return String.format("{\"id\": %d, \"eventName\": %s}",
                id, eventName);
    }
}
