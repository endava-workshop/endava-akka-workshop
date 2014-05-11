package com.akka.ws.neo4j.rest.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.old.Neo4jRestDaoImpl;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

public class CustomTest implements Neo4jQueryInterface{

	private RestAPIFacade restApi;

	private QueryEngine engine;

	private RestIndex<Node> index;

	private boolean addLabels;
	
	private Neo4jRestDaoImpl service = new Neo4jRestDaoImpl("http://localhost:7474/db/data", true);

	@Before
	public void setUp() {
		restApi = new RestAPIFacade("http://localhost:7474/db/data");
		engine = new RestCypherQueryEngine(restApi);
		
//		service.removeAllDomains();
	}
	
	@Test
	public void addUniqueDomain(){
		Domain domain = getDomain("test");
		try{
		engine.query(CREATE_DOMAIN, getDomainMap(domain));
		
		
		
		engine.query(CREATE_DOMAIN, getDomainMap(domain));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		assertEquals(1, service.countAllDomains());
	}
	
	@Test
	public void testAutoIndex(){
		GraphDatabaseService restGraphDb = new RestGraphDatabase( "http://localhost:7474/db/data" );
		
		AutoIndexer autoIndexer = restGraphDb.index().getNodeAutoIndexer();
		ReadableIndex index = autoIndexer.getAutoIndex();
		
		String name = index.getName();
		System.out.println("name : " + name);
		
		IndexHits hits= autoIndexer.getAutoIndex().get("dname", "test");
		System.out.println(hits.hasNext());
		
		Domain domain = getDomain("test2");
		engine.query(CREATE_DOMAIN, getDomainMap(domain));
		
		hits= autoIndexer.getAutoIndex().get("dname", "test2");
		assertTrue(hits.hasNext());
		
		hits= autoIndexer.getAutoIndex().get("dname", "test");
		assertTrue(hits.hasNext());
		
	}
	
	private Map<String, Object> getDomainMap(Domain domain){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domain.getName());
		paramMap.put(DOMAIN_STATUS, domain.getDomainStatus().toString());
		paramMap.put(COOL_DOWN_PERIOD, domain.getCoolDownPeriod());
		paramMap.put(CRAWLED_AT, domain.getCrawledAt());
		return paramMap;
	}
	
	private Domain getDomain(String dname){
		return new Domain(dname, 10, 10, DomainStatus.FOUND);
	}
}
