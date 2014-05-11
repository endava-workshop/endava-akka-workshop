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

	private UrlService service;

	@Before
	public void setUp() {
		service = new RestNeo4jUrlServiceImpl();
	}

	@Test
	public void testCreateDomain() {
		for(int i = 0; i< 100; i++){
			service.addDomainUrl("Wikipedia" + i, "www.wikipedia.com" + i, 1000L);
		}
	}

	@Test
	public void testFindDomains() {
		List<DomainURL> domainList = service
				.findDomains(new PageRequest(0, 10));
		assertTrue(domainList.size() > 0);
	}

	@Test
	public void testRemoveDomain() {
		service.removeDomainUrl("www.wikipedia.com");
	}

	@Test
	public void testAddDomainLink() {
		List<DomainLink> domainLinks = new ArrayList<DomainLink>();
		DomainURL domainURL = new DomainURL("Wikipedia", "www.wikipedia.com");
		SimpleURL simpleURL = new SimpleURL("www.wikipedia11", "wiki11",
				"status", 0, DateTime.now().clicks());
		domainLinks.add(new DomainLink(domainURL, simpleURL));
		service.addDomainLinks(domainLinks);
	}

	@Test
	public void testGetDomainLinks() {
		Collection<SimpleURL> list = service.findURLs("www.yahoo.com",
				"NOT_VISITED", 0, 2);
		assertEquals(2, list.size());
	}

	@Test
	public void testUpdateLinkStatus() {
		Collection<SimpleURL> list = service.findURLs("www.yahoo.com",
				"TEST_STATUS", 0, 2);
		assertEquals(0, list.size());

		List<String> urlList = new ArrayList<String>();
		urlList.add("www.yahoo6.com");

		service.updateSimpleUrlsStatus(urlList, "TEST_STATUS");
		list = service.findURLs("www.yahoo.com", "TEST_STATUS", 0, 2);
		assertEquals(1, list.size());

		service.updateSimpleUrlsStatus(urlList, "VISITED");

	}

	@Test
	public void testUpdateLinkErrorCount() {
		service.updateSimpleUrlErrorStatus("www.yahoo6.com", 1);
		Collection<SimpleURL> list = service.findURLs("www.yahoo.com",
				"VISITED", 0, 2);
		assertEquals(1, list.size());

		assertEquals(list.iterator().next().getErrorCount(), 1);

	}

	@Test
	public void testCountMethods() {
		// count all nodes
		long count = service.countAllNodes();
		assertEquals(8, count);
		// count all domains
		count = service.countAllDomains();
		assertEquals(2, count);
		// count all links
		count = service.countAllLinks();
		assertEquals(6, count);
		// count domain links
		count = service.countDomainLinks("www.wikipedia.com");
		assertEquals(2, count);

	}

	@Test
	public void testAddDomainLinks_STRESS() throws Exception {
		int numberOfLinks = 100;
		List<DomainLink> domainLinks = new ArrayList<>();
		DomainURL domain1 = new DomainURL("d1", "n1");
		for (int i = 0; i < numberOfLinks; i++) {
			domainLinks.add(new DomainLink(domain1, new SimpleURL("url" + i,
					"n" + i, "new")));
		}
		
		long time = System.currentTimeMillis();
		service.addDomainLinks(domainLinks);
		service.addDomainLinks(domainLinks);
		System.out.println("addDomainLinks : " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		
		assertEquals(numberOfLinks, service.countAllLinks());
		System.out.println("countAllLinks : " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		assertEquals(1, service.countAllDomains());
		System.out.println("countAllDomains : " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();

	}

	@Test
	public void testAddDomainLinks() throws Exception {
		List<DomainLink> domainLinks = new ArrayList<>();
		DomainURL domain1 = new DomainURL("d1", "n1");
		domainLinks.add(new DomainLink(domain1, new SimpleURL("url1", "n1",
				"new")));
		domainLinks.add(new DomainLink(domain1, new SimpleURL("url2", "n2",
				"new")));
		DomainURL domain2 = new DomainURL("d2", "n2");
		domainLinks.add(new DomainLink(domain2, new SimpleURL("url3", "n1",
				"new")));
		domainLinks.add(new DomainLink(domain2, new SimpleURL("url4", "n2",
				"new")));

		service.addDomainLinks(domainLinks);

		assertEquals(4, service.countAllLinks());
		assertEquals(2, service.countAllDomains());
	}
	@Test
	public void testAddDuplicateDomains() throws Exception {
		long time = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++){
			service.addDomainUrl("domain" + i, "durl", 0);
		}
		System.out.println("time : " + (System.currentTimeMillis() - time) + " ms");
		assertEquals(1, service.countAllDomains());
	}

	@Test
	public void testAddDuplicateDomainLinks() throws Exception {
		List<DomainLink> domainLinks1 = new ArrayList<>();
		DomainURL domain1 = new DomainURL("d1", "n1");
		domainLinks1.add(new DomainLink(domain1, new SimpleURL("url1", "n1",
				"new")));
		domainLinks1.add(new DomainLink(domain1, new SimpleURL("url2", "n2",
				"new")));
		DomainURL domain2 = new DomainURL("d2", "n2");
		domainLinks1.add(new DomainLink(domain2, new SimpleURL("url3", "n1",
				"new")));
		domainLinks1.add(new DomainLink(domain2, new SimpleURL("url4", "n2",
				"new")));
		List<DomainLink> domainLinks2 = new ArrayList<>();
		DomainURL domain3 = new DomainURL("d3", "n3");
		domainLinks2.add(new DomainLink(domain3, new SimpleURL("url1", "n2",
				"new")));
		domainLinks2.add(new DomainLink(domain3, new SimpleURL("url5", "n1",
				"new")));
		domainLinks2.add(new DomainLink(domain2, new SimpleURL("url3", "n2",
				"new")));
		domainLinks2.add(new DomainLink(domain2, new SimpleURL("url7", "n1",
				"new")));

		service.addDomainLinks(domainLinks1);
		service.addDomainLinks(domainLinks2);

		assertEquals(6, service.countAllLinks());
		assertEquals(3, service.countAllDomains());
	}
}
