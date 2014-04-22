package entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

@NodeEntity
public class DomainURL {

	final String CONTAINS = "CONTAINS";
	final String LINKS_TO = "LINKS_TO";

	@GraphId
	Long id;


	String name;
    @Indexed(unique = true, indexType = IndexType.SIMPLE, indexName = "url")
	String address;
    long coolDownPeriod = 1000;
	@RelatedTo(type = CONTAINS, direction = Direction.OUTGOING)
//	@Fetch - removed as it causes performance issues
	Set<SimpleURL> internalUrlSet = new HashSet<>();

    @RelatedTo(type = LINKS_TO, direction = Direction.OUTGOING)
    Set<SimpleURL> externalUrlSet = new HashSet<>();

	public DomainURL() {
	}

    public DomainURL(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public DomainURL(String name, String address, long coolDownPeriod) {
        this.name = name;
        this.address = address;
        this.coolDownPeriod = coolDownPeriod;
    }

    public void addInternalUrl(SimpleURL simpleURL){
		internalUrlSet.add(simpleURL);
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCoolDownPeriod() {
        return coolDownPeriod;
    }

    public void setCoolDownPeriod(long coolDownPeriod) {
        this.coolDownPeriod = coolDownPeriod;
    }

    public Set<SimpleURL> getInternalUrlSet() {
        return internalUrlSet;
    }

    public void setInternalUrlSet(Set<SimpleURL> internalUrlSet) {
        this.internalUrlSet = internalUrlSet;
    }

    public Set<SimpleURL> getExternalUrlSet() {
        return externalUrlSet;
    }

    public void setExternalUrlSet(Set<SimpleURL> externalUrlSet) {
        this.externalUrlSet = externalUrlSet;
    }
}
