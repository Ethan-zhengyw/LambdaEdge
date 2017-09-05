package edgecloud.lambda.repository;

import edgecloud.lambda.entity.EventResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventResultRepository extends JpaRepository<EventResult, Integer> {

//    public EventResult findByEventName(String eventName);

}
