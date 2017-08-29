package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer eventId;

    private String eventName;

}
