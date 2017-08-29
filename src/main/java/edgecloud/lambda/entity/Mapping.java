package edgecloud.lambda.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Mapping {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer func_id;

    private String event_name;

}
