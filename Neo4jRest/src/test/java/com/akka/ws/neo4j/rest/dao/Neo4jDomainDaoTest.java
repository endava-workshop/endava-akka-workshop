package com.akka.ws.neo4j.rest.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.dao.Neo4jDomainDao;
import com.akka.ws.neo4j.dao.impl.Neo4jDomainDaoImpl;
import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;

public class Neo4jDomainDaoTest {

	String dbUrl = "http://localhost:7474/db/data";

	private Neo4jDomainDao service;

	private long startTime;

	@Before
	public void setUp() {

		service = new Neo4jDomainDaoImpl(dbUrl);

		service.cleanDatabase();
		startTime = System.currentTimeMillis();
	}

	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - startTime) + " ms");
	}

	@Test
	public void testUniqueDomain_OneByOne() {
		int newNodes = 0;
		int oldNodes = 0;
		for (int i = 0; i < 100; i++) {
			if (service.addDomain(getDomain("d_" + i))) {
				newNodes++;
			} else {
				oldNodes++;
			}
		}
		System.out.println("new nodes : " + newNodes);
		System.out.println("old nodes : " + oldNodes);

	}
	
	
	@Test
	public void testUniqueDomain_BatchDuplicate() {
		testUniqueDomain_Batch();
		testUniqueDomain_Batch();
	}
	
	@Test
	public void testUniqueDomain_Batch() {
		List<Domain> domainList = new ArrayList<Domain>();
		
		for (int i = 0; i < 1000; i++) {
			domainList.add(getDomain("d_" + i));
		}
		
		Set<Domain> newDomainList = new HashSet<Domain>();
		for(Domain domain : domainList){
			if(!service.domainExists(domain)){
				newDomainList.add(domain);
			}
		}
		
		System.out.println("new nodes : " + newDomainList.size());
		
		service.addDomainsInBatch(newDomainList);
		
	}

	private Domain getDomain(String dname) {
		return new Domain(dname, 10, 10, DomainStatus.FOUND);
	}
}
