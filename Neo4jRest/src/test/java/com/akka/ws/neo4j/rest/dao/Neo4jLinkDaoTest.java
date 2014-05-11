package com.akka.ws.neo4j.rest.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.dao.Neo4jLinkDao;
import com.akka.ws.neo4j.dao.impl.Neo4jLinkDaoImpl;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.enums.LinkStatus;

public class Neo4jLinkDaoTest {

	String dbUrl = "http://localhost:7474/db/data";

	private Neo4jLinkDao service;

	private long startTime;

	@Before
	public void setUp() {

		service = new Neo4jLinkDaoImpl(dbUrl);

		service.cleanDatabase();
		startTime = System.currentTimeMillis();
	}

	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - startTime) + " ms");
	}

	@Test
	public void testUniqueLink_OneByOne() {
		Link link = getLink("url", "domain");
		
		service.addLink(link);
		
		System.out.println(service.linkExists(link));
		
		service.addLink(link);

	}
	
	private Link getLink(String url, String domain) {
		return new Link(domain, url, LinkStatus.NOT_VISITED);
	}
}
