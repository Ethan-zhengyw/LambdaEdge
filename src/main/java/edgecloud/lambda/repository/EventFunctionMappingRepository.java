package edgecloud.lambda.repository;

import edgecloud.lambda.entity.EventFunctionMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFunctionMappingRepository extends JpaRepository<EventFunctionMapping, Integer> {

    public List<EventFunctionMapping> findByEventName(String eventName);
    public EventFunctionMapping findByEventNameAndFuncId(String eventName, Integer funcId);

}
