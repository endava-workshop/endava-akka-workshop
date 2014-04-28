package service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import entity.DomainLink;
import entity.DomainURL;
import entity.SimpleURL;
import service.UrlService;
import service.impl.neo4j.rest.RestNeo4jUrlServiceImpl;
import spray.http.DateTime;

public class RestNeo4jUrlServiceImplTest {

	private UrlService urlsService;
	
	@Before
	public void setUp(){
		urlsService = new RestNeo4jUrlServiceImpl();
	}
	
	@Test
	public void testCreateDomain(){
		urlsService.addDomainUrl("Wikipedia", "www.wikipedia.com", 1000L);
	}
	
	@Test
	public void testFindDomains(){
		List<DomainURL> domainList = urlsService.findDomains(new PageRequest(0, 10));
		assertTrue(domainList.size() > 0);
	}
	
	@Test
	public void testRemoveDomain(){
		urlsService.removeDomainUrl("www.wikipedia.com");
	}
	
	@Test
	public void testAddDomainLink(){
		List<DomainLink> domainLinks = new ArrayList<DomainLink>();
		DomainURL domainURL = new DomainURL("Wikipedia", "www.wikipedia.com");
		SimpleURL simpleURL = new SimpleURL("www.wikipedia11", "wiki11", "status", 0, DateTime.now().clicks());
		domainLinks.add(new DomainLink(domainURL, simpleURL));
		urlsService.addDomainLinks(domainLinks);
	}
}
