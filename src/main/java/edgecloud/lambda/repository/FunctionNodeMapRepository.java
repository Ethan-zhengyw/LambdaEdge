package edgecloud.lambda.repository;

import edgecloud.lambda.entity.FunctionNodeMap;
import edgecloud.lambda.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunctionNodeMapRepository extends JpaRepository<FunctionNodeMap, Integer> {
    public List<FunctionNodeMap> findByFuncId(Integer funcId);

    FunctionNodeMap findByFuncIdAndNodeId(Integer funcId, Integer nodeId);
}
