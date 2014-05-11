package com.akka.ws.neo4j.rest.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.dao.Neo4jLinkDao;
import com.akka.ws.neo4j.dao.Neo4jRelationDao;
import com.akka.ws.neo4j.dao.impl.Neo4jLinkDaoImpl;
import com.akka.ws.neo4j.dao.impl.Neo4jRelationDaoImpl;
import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.entity.Relation;
import com.akka.ws.neo4j.enums.LinkStatus;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

public class Neo4jRelationDaoTest implements Neo4jQueryInterface {

	String dbUrl = "http://localhost:7474/db/data";

	private Neo4jRelationDao relationDao;
	
	private Neo4jLinkDao linkDao;
	
	private long startTime;

	@Before
	public void setUp() {

		relationDao = new Neo4jRelationDaoImpl(dbUrl);
		linkDao = new Neo4jLinkDaoImpl(dbUrl);
		
		relationDao.cleanDatabase();
		startTime = System.currentTimeMillis();
	}

	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - startTime) + " ms");
	}

	@Test
	public void testUniqueRelation_OneByOne() {
		Link link1 = new Link("domain", "url1", "status");
		Link link2 = new Link("domain", "url2", "status");
		linkDao.addLink(link1);
		linkDao.addLink(link2);
		
		Relation relation = new Relation(link1.getUrl(), link2.getUrl(), REL_LINKS_TO);
		
		relationDao.addRelation(relation);
		
		System.out.println(relationDao.relationExists(relation));
		
		relationDao.addRelation(relation);

	}
	
	private Link getLink(String url, String domain) {
		return new Link(domain, url, LinkStatus.NOT_VISITED);
	}
}
