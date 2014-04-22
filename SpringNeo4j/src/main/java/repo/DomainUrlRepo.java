package repo;

import entity.DomainURL;
import entity.SimpleURL;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface DomainUrlRepo extends GraphRepository<DomainURL> {

    @Query("MATCH (d: DomainURL) where d.address={0} RETURN d LIMIT 1")
    DomainURL findOneByUrl(String url);
}
