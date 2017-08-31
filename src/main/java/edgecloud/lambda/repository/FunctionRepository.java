package edgecloud.lambda.repository;

import edgecloud.lambda.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunctionRepository extends JpaRepository<Function, Integer> {
    Function findByFuncNameAndFuncVersion(String funcName, Integer funcVersion);
    public Function findById(Integer funcId);
}
