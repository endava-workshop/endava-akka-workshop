package service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import repo.DomainUrlRepo;
import repo.SimpleUrlRepo;
import entity.DomainUrl;
import entity.SimpleUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-appContext.xml"})
@Transactional
public class TestUrlService {

	@Autowired
	DomainUrlRepo domainRepo;
	
	@Autowired
	SimpleUrlRepo simpleUrlRepo;
	
	@Autowired
	UrlService urlService;
	
	@Before
	public void setUp() {		
		
	}
	
	@Test
	public void testDomainSave(){
		DomainUrl domain = urlService.addDomainUrl("Domain_1", "www.domain_1.com");
		
		Assert.assertNotNull(domain);
		Assert.assertEquals(1, domainRepo.count());

	}
	
	@Test
	public void testSaveSimpleUrl(){
		DomainUrl domain = urlService.addDomainUrl("Domain_1", "www.domain_1.com");
		SimpleUrl simpleUrl = urlService.addSimpleUrl("page1", "www.domain_1.com/page_1", domain.getName());
		
		Assert.assertNotNull(simpleUrl);
		Assert.assertEquals(1, simpleUrlRepo.count());
	}

	@Test
	public void testRemoveSimpleUrl(){
		DomainUrl domain = urlService.addDomainUrl("Domain_1", "www.domain_1.com");
		SimpleUrl simpleUrl = urlService.addSimpleUrl("page1", "www.domain_1.com/page_1", domain.getName());

		Assert.assertNotNull(simpleUrl);
		Assert.assertEquals(1, simpleUrlRepo.count());
		
		urlService.removeSimpleUrl(simpleUrl.getName());
		Assert.assertEquals(0, simpleUrlRepo.count());
	}

	@Test
	public void testRemoveDomain(){
		DomainUrl domain = urlService.addDomainUrl("Domain_1", "www.domain_1.com");
		urlService.addSimpleUrl("page1", "www.domain_1.com/page_1", domain.getName());

		urlService.removeDomainUrl(domain.getName());
		Assert.assertEquals(0, domainRepo.count());
	}

	@Test
	public void testMultipleDomains(){
		DomainUrl domain_1 = urlService.addDomainUrl("Domain_1", "www.domain_1.com");
		SimpleUrl simpleUrl_1 = urlService.addSimpleUrl("page1", "www.domain_1.com/page_1", domain_1.getName());

		DomainUrl domain_2 = urlService.addDomainUrl("Domain_2", "www.domain_2.com");
		SimpleUrl simpleUrl_2 = urlService.addSimpleUrl("page2", "www.domain_2.com/page_2", domain_2.getName());

		Assert.assertEquals(2, simpleUrlRepo.count());
		
		urlService.removeSimpleUrl(simpleUrl_1.getName());
		
		urlService.removeDomainUrl(domain_1.getName());
		
		Assert.assertEquals(1, domainRepo.count());
	}

//	@After
//	public void tearDown() {
//		urlService.removeAllDomains();
//	}
	
	private void populateDatabase(){
		DomainUrl wikiDomain = new DomainUrl("wikipedia", "www.wikipedia.org");
		
		SimpleUrl wikiSimpleUrl = new SimpleUrl("Wiki English", "http://en.wikipedia.org/wiki/Main_Page");
		wikiDomain.addInternalUrl(wikiSimpleUrl);
		simpleUrlRepo.save(wikiSimpleUrl);
		
		wikiSimpleUrl = new SimpleUrl("Wiki Italiano", "http://it.wikipedia.org/wiki/Pagina_principale");
		wikiDomain.addInternalUrl(wikiSimpleUrl);
		simpleUrlRepo.save(wikiSimpleUrl);
		
		wikiSimpleUrl = new SimpleUrl("Wiki Deutsch", "http://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
		wikiDomain.addInternalUrl(wikiSimpleUrl);
		simpleUrlRepo.save(wikiSimpleUrl);
		
		//save
		domainRepo.save(wikiDomain);
	}
}
