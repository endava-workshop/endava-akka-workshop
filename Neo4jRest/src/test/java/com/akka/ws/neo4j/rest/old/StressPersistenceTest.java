package com.akka.ws.neo4j.rest.old;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.old.Neo4jRestDaoImpl;

public class StressPersistenceTest {

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
		// service.removeAllDomains();
	}

	@Test
	public void testCreateDomainsOneByOne() {
		List<Domain> domainList = getDomains(0, 1000);
		for (Domain domain : domainList) {
			service.addDomain(domain);
		}
	}

	@Test
	public void testCreateDomainsInBatch() {
		List<Domain> domainList = getDomains(0, 1000);
		service.addDomainsInBatch(domainList);
	}

	@Test
	public void testCheckAndAddDomainsInBatch() {
		// database is clean
		List<Domain> existingDomainList = getDomains(0, 1000);
		service.addDomainsInBatch(existingDomainList);

		List<Domain> domainList = getDomains(500, 1500);

		List<Domain> newDomainList = new ArrayList<Domain>();

		newDomainList = service.getNewDomainsFrom(domainList);

		service.addDomainsInBatch(newDomainList);
	}

	@Test
	public void testCheckAndAddDomainsInBatchV2() {
		// database has 100 000 nodes
		long time = System.currentTimeMillis();
		int totalPages = 100;
		int pageSize = 1000;
		System.out.println("start insertion of " + (totalPages * pageSize) + " domains");
		for (int i = 0; i < totalPages; i++) {
			long batchTime = System.currentTimeMillis();
			
			int startIndex = i * pageSize;
			List<Domain> existingDomainList = getDomains(startIndex, pageSize);
			assertEquals(pageSize, existingDomainList.size());

			service.addDomainsInBatch(existingDomainList);
			
			System.out.println("added " + pageSize + " domains in " + (System.currentTimeMillis() - batchTime) + " ms");
			
		}
		System.out.println("added " + (totalPages * pageSize) + " domains in : " + (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();

		List<Domain> domainList = getDomains(500, 1500);

		List<Domain> newDomainList = new ArrayList<Domain>();

		newDomainList = service.getNewDomainsFrom(domainList);

		System.out.println("identified " + newDomainList.size() + " new domains in : " + (System.currentTimeMillis() - time));

		service.addDomainsInBatch(newDomainList);

		System.out.println("added new " + newDomainList.size() + " domains in : " + (System.currentTimeMillis() - time));

	}

	private List<Domain> getDomains(int startIndex, int howManny) {
		List<Domain> domainList = new ArrayList<Domain>();
		int limit = startIndex + howManny;
		for (int i = startIndex; i < limit; i++) {
			domainList.add(new Domain("name" + i, 10, 10, DomainStatus.FOUND));
		}
		return domainList;
	}
}
