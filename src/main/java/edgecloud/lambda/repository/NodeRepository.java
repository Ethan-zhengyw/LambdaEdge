package edgecloud.lambda.repository;

import edgecloud.lambda.entity.Function;
import edgecloud.lambda.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Integer> {
}
