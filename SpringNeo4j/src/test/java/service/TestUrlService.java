package service;

import static junit.framework.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import repo.DomainUrlRepo;
import repo.SimpleUrlRepo;
import entity.DomainUrl;
import entity.SimpleUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-appContext.xml"})
public class TestUrlService {

	@Autowired
	DomainUrlRepo domainRepo;
	
	@Autowired
	SimpleUrlRepo simpleUrlRepo;
	
	@Before
	public void setUp() {		
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
	
	@Test
	public void testFindAll(){
		long domainNumber = domainRepo.count();
		assertEquals(1, domainNumber);

		long simpleUrlNumber = simpleUrlRepo.count();
		assertEquals(3, simpleUrlNumber);

		
	}
	
	@After
	public void tearDown() {
		domainRepo.deleteAll();
		simpleUrlRepo.deleteAll();
	}
}
