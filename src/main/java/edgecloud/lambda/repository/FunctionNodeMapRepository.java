package edgecloud.lambda.repository;

import edgecloud.lambda.entity.FunctionNodeMap;
import edgecloud.lambda.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunctionNodeMapRepository extends JpaRepository<FunctionNodeMap, Integer> {
}
