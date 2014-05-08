package com.akka.ws.neo4j.rest.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.service.Neo4jRestPersistenceImpl;
import com.akka.ws.neo4j.service.PersistenceService;

public class StressPersistenceTest {

	private PersistenceService service;

	private long startTime;

	@Before
	public void setUp() {
		service = new Neo4jRestPersistenceImpl("http://localhost:7474/db/data", true);
		service.removeDomains();
		startTime = System.currentTimeMillis();
	}

	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - startTime) + " ms");
	}

	@Test
	public void testCreateDomain() {
		String domainName = "d";
		int dNo = 1000;
		for (int i = 0; i < 1000; i++) {
			service.addDomain(new Domain(domainName + i, 1000, 0, DomainStatus.PAUSED));
		}
		assertEquals(dNo, service.countAllDomains());
	}
	
	@Test
	public void testBatchCreateDomain() {
		String domainName = "d";
		int dNo = 1000;
		List<Domain> domainList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			domainList.add(new Domain(domainName + i, 1000, 0, DomainStatus.PAUSED));
		}
		service.addDomainsInBatch(domainList);
		
		assertEquals(dNo, service.countAllDomains());
	}
}
