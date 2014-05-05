package service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import service.UrlService;
import service.impl.neo4j.rest.RestNeo4jUrlServiceImpl;
import spray.http.DateTime;
import entity.DomainLink;
import entity.DomainURL;
import entity.SimpleURL;

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
	
	@Test
	public void testGetDomainLinks(){
		Collection<SimpleURL> list = urlsService.findURLs("www.yahoo.com", "NOT_VISITED", 0, 2);
		assertEquals(2, list.size());
	}
	
	@Test
	public void testUpdateLinkStatus(){
		Collection<SimpleURL> list = urlsService.findURLs("www.yahoo.com", "TEST_STATUS", 0, 2);
		assertEquals(0, list.size());
		
		List<String> urlList = new ArrayList<String>();
		urlList.add("www.yahoo6.com");
		
		urlsService.updateSimpleUrlsStatus(urlList, "TEST_STATUS");
		list = urlsService.findURLs("www.yahoo.com", "TEST_STATUS", 0, 2);
		assertEquals(1, list.size());
		
		urlsService.updateSimpleUrlsStatus(urlList, "VISITED");

	}
	
	@Test
	public void testUpdateLinkErrorCount(){
		urlsService.updateSimpleUrlErrorStatus("www.yahoo6.com", 1);
		Collection<SimpleURL> list = urlsService.findURLs("www.yahoo.com", "VISITED", 0, 2);
		assertEquals(1, list.size());
		
		assertEquals(list.iterator().next().getErrorCount(), 1);

	}
}
