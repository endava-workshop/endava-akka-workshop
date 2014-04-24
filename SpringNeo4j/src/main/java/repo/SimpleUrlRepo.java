package repo;

import entity.SimpleURL;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

public interface SimpleUrlRepo extends GraphRepository<SimpleURL> {
    @Query("MATCH (u: SimpleURL) where u.url={0} RETURN u LIMIT 10")
    List<SimpleURL> findByUrl(String url);
    @Query("MATCH (u: SimpleURL) where u.url={0} RETURN u LIMIT 1")
    SimpleURL findOneByUrl(String url);

    @Query("MATCH (u: SimpleURL) where u.url={0} SET u.status = {1}" )
    void setUrlStatus(String url, String status);

    @Query("MATCH (u: SimpleURL) where u.url={0} SET u.errorCount = u.errorCount + {1}" )
    void setUrlError(String url, int errorDelta);

//    List<SimpleURL> findByName(String name);
}
