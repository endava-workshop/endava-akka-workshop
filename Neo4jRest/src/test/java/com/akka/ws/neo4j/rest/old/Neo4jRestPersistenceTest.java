package com.akka.ws.neo4j.rest.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.old.Neo4jRestDaoImpl;

public class Neo4jRestPersistenceTest {

	private Neo4jRestDaoImpl service;

	private long startTime;
	
	@Before
	public void setUp() {
		service = new Neo4jRestDaoImpl("http://localhost:7474/db/data", true);
		service.removeAllDomains();
		startTime = System.currentTimeMillis();
	}
	
	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - startTime) + " ms");
		service.removeAllDomains();
	}

	@Test
	public void testCreateFindDomain() {
		List<Domain> domainList = getDomains(0, 1);
		Domain domain = domainList.get(0);
		service.addDomain(domain);
		Domain found = service.findDomain(domain.getName());
		assertNotNull(found);
		assertEquals(domain.getName(), found.getName());
		
	}

	@Test
	public void testCreateDomainsInBatch() {
		List<Domain> domainList = getDomains(0, 1000);
		service.addDomainsInBatch(domainList);
	}
	
	@Test
	public void testCheckAndAddDomainsInBatch() {
		List<Domain> existingDomainList = getDomains(0, 1000);
		service.addDomainsInBatch(existingDomainList);
		
		List<Domain> domainList = getDomains(500, 1500);
		
		List<Domain> newDomainList = new ArrayList<Domain>();
		for(Domain domain : domainList){
			if(service.findDomain(domain.getName()) == null){
				newDomainList.add(domain);
			}
		}
		
		service.addDomainsInBatch(newDomainList);
	}

	private List<Domain> getDomains(int startIndex, int howManny){
		List<Domain> domainList = new ArrayList<Domain>();
		for(int i = startIndex; i < howManny; i++){
			domainList.add(new Domain("name" + i, 10, 10, DomainStatus.FOUND));
		}
		return domainList;
	}
}
