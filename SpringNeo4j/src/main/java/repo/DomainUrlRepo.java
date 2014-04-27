package repo;

import entity.DomainURL;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface DomainUrlRepo extends GraphRepository<DomainURL> {

    @Query("MATCH (d: DomainURL) where d.address={0} RETURN d LIMIT 1")
    DomainURL findOneByUrl(String url);

//    @Query("MATCH (d: DomainURL) RETURN d.name as name, d.address as address, d.coolDownPeriod as coolDownPeriod SKIP {1} LIMIT {0}")
    @Query("MATCH (d: DomainURL)-[:`CONTAINS`]->(url {status: 'NOT_VISITED'})  RETURN distinct d.name SKIP {1} LIMIT {0}")
    List<String> findAll(int maxResults, int skip);
}
