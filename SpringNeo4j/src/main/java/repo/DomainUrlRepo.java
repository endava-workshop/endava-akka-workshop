package repo;

import org.springframework.data.neo4j.repository.GraphRepository;

import entity.DomainUrl;

public interface DomainUrlRepo extends GraphRepository<DomainUrl> {

}
