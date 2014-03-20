package entity;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;

@NodeEntity
public class SimpleUrl {

	@GraphId Long id;
	
	@Indexed(indexType = IndexType.SIMPLE, indexName = "name")
	String name;
	
	String url;
	
	public SimpleUrl(){
		
	}
	
	public SimpleUrl(String name, String url) {
		this.name = name;
		this.url = url;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
}
