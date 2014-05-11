package com.akka.ws.neo4j.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.DomainLink;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.enums.LinkStatus;
import com.akka.ws.neo4j.service.PersistenceService;
import com.akka.ws.neo4j.service.PersistenceServiceImpl;

public class PersistenceServiceTest {

	private PersistenceService service;
	
	@Before
	public void setUp(){
		service = new PersistenceServiceImpl();
		service.cleanDatabase();
	}
	
	@Test
	public void testNodesWithRelations(){
		Domain domain = getDomain();
		
		service.addDomain(domain);
		
		
		List<DomainLink> domainLinks = getPage1For(domain);
		
		service.addDomainLinks(domainLinks);
		
		
		
		
	}
	
	private List<DomainLink> getPage1For(Domain domain){
		List<DomainLink> domainLinkList = new ArrayList<DomainLink>();
		Link la1 = new Link(domain.getName(), "la1", LinkStatus.VISITED);
		Link la2 = new Link(domain.getName(), "la2", "la1", LinkStatus.NOT_VISITED);
		domainLinkList.add(new DomainLink(domain, la1));
		domainLinkList.add(new DomainLink(domain, la2));
		
		Link lb1 = new Link("DB", "lb1", "la1");
		Link lc1 = new Link("DC", "lc1", "la1");
		Link lb2 = new Link("DB", "lb2", "la1");
		domainLinkList.add(new DomainLink(domain, lb1));
		domainLinkList.add(new DomainLink(domain, lc1));
		domainLinkList.add(new DomainLink(domain, lb2));
		
		return domainLinkList;
		
	}
	
	private List<DomainLink> getPage2For(Domain domain){
		List<DomainLink> domainLinkList = new ArrayList<DomainLink>();
		
		return domainLinkList;
		
	}
	
	private Domain getDomain(){
		return new Domain("DA", 0, 0);
		
	}
}
