package edgecloud.lambda.repository;

import edgecloud.lambda.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    public List<Event> findById(Integer id);

}
