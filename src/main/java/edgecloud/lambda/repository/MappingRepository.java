package edgecloud.lambda.repository;

import edgecloud.lambda.entity.MyMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MappingRepository extends JpaRepository<MyMapping, Integer> {

    public List<MyMapping> findById(Integer id);

}
