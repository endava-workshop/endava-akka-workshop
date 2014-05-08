package com.akka.ws.neo4j.rest.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.service.Neo4jRestPersistenceImpl;
import com.akka.ws.neo4j.service.PersistenceService;

public class Neo4jRestPersistenceTest {

	private PersistenceService service;

	private long startTime;
	
	@Before
	public void setUp() {
		service = new Neo4jRestPersistenceImpl("http://localhost:7474/db/data", true);
		startTime = System.currentTimeMillis();
	}
	
	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - startTime) + " ms");
	}

	@Test
	public void testCreateDomain() {
		service.addDomain(new Domain("www.wikipedia.com", 1000, 0, DomainStatus.PAUSED));
	}
}
