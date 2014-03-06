package repo;

import org.springframework.data.neo4j.repository.GraphRepository;

import entity.SimpleUrl;

public interface SimpleUrlRepo extends GraphRepository<SimpleUrl> {

}
