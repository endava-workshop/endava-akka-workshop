package entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class DomainUrl {

	final String CONTAINS = "CONTAINS";

	@GraphId
	Long id;

	@Indexed(indexName = "name")
	String name;

	String address;

	@RelatedTo(type = CONTAINS, direction = Direction.OUTGOING)
	Set<SimpleUrl> internalUrlSet;

	public DomainUrl() {
	}

	public DomainUrl(String value, String address) {
		this.name = value;
		this.address = address;
		internalUrlSet = new HashSet<SimpleUrl>();
	}
	
	public void addInternalUrl(SimpleUrl simpleUrl){
		internalUrlSet.add(simpleUrl);
	}
}
