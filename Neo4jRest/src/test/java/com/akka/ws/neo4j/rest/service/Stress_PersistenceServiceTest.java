package com.akka.ws.neo4j.rest.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.DomainLink;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.enums.LinkStatus;
import com.akka.ws.neo4j.service.PersistenceService;
import com.akka.ws.neo4j.service.PersistenceServiceImpl;

public class Stress_PersistenceServiceTest {

	private PersistenceService service;

	private long time;

	@Before
	public void setUp() {
		service = new PersistenceServiceImpl(true, 1500);
		service.cleanDatabase();
		time = System.currentTimeMillis();
	}

	@After
	public void cleanUp() {
		System.out.println("test time : " + (System.currentTimeMillis() - time));
	}

	@Test
	public void testNodesWithRelations() {
		int totalDomains = 300;
		int intLinksPerPage = 10;
		int extLinksPerPage = 20;

		Set<Domain> domainSet = getDomainSet(totalDomains);

		service.addDomainsInBatch(domainSet);

		time = System.currentTimeMillis();

		int totalDomainLinks = 0;
		/*
		 * simulate on page crawled per domain
		 */
		int pageNumber = 0;

		while (pageNumber < 10) {
			for (Domain domain : domainSet) {
				List<DomainLink> domainLinks = getPageFor(domain, pageNumber, intLinksPerPage, extLinksPerPage);

				service.addDomainLinks(domainLinks);
				totalDomainLinks += domainLinks.size();
			}
			pageNumber++;
//			try {
//				Thread.sleep(30 * 1000);
//			} catch (InterruptedException e) {
//
//			}
		}

		System.out.println("added " + totalDomainLinks + " domainLinks");
	}

	private Set<Domain> getDomainSet(int totalDomains) {
		Set<Domain> domainSet = new HashSet<Domain>(totalDomains);
		for (int i = 0; i < totalDomains; i++) {
			domainSet.add(new Domain("d_" + i, 0, 0));
		}
		return domainSet;
	}

	private List<DomainLink> getPageFor(Domain domain, int pageNumber, int internaLinks, int externalLinks) {
		List<DomainLink> domainLinkList = new ArrayList<DomainLink>();
		// add current page
		Link currentLink = new Link(domain.getName(), domain.getName() + "l_" + pageNumber, LinkStatus.VISITED);
		domainLinkList.add(new DomainLink(domain, currentLink));

		// add internal links
		for (int i = 0; i < internaLinks; i++) {
			String url = domain.getName() + "l_" + (pageNumber + i + 1);
			Link intLink = new Link(domain.getName(), url, currentLink.getUrl(), LinkStatus.NOT_VISITED);
			domainLinkList.add(new DomainLink(domain, intLink));
		}

		// add external links
		for (int i = 0; i < externalLinks; i++) {
			String domainName = "ext" + domain.getName() + pageNumber;
			String url = domainName + "l_" + (pageNumber + i + 1);
			Link extLink = new Link(domainName, url, currentLink.getUrl(), LinkStatus.NOT_VISITED);
			domainLinkList.add(new DomainLink(domain, extLink));
		}

		return domainLinkList;

	}

	private List<DomainLink> getPage2For(Domain domain) {
		List<DomainLink> domainLinkList = new ArrayList<DomainLink>();

		return domainLinkList;

	}

	private Domain getDomain() {
		return new Domain("DA", 0, 0);

	}
}
