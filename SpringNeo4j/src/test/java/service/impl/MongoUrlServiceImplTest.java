package service.impl;

import entity.DomainLink;
import entity.DomainURL;
import entity.SimpleURL;
import metrics.MetricsConfig;
import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import service.impl.mongo.MongoDomainURL;
import service.impl.mongo.MongoSimpleURL;
import util.MongoTest;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MongoUrlServiceImplTest extends MongoTest {

    private MongoCollection domainRepo;
    private MongoCollection urlRepo;
    private MongoUrlServiceImpl service;
    private static MetricsConfig metricsConfig = new MetricsConfig();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        domainRepo = jongo.getCollection("domains");
        urlRepo = jongo.getCollection("urls");
        service = new MongoUrlServiceImpl();
        service.metricsConfig = metricsConfig;
        service.domainRepo = domainRepo;
        service.urlRepo = urlRepo;
        service.init();
    }

    @After
    public void tearDown() throws Exception {
        domainRepo.drop();
        urlRepo.drop();
    }

    @Test
    public void testAddDomainUrl() throws Exception {

        service.addDomainUrl("test.com", "www.test.com", 1000);

        assertEquals(1, domainRepo.count());
        MongoDomainURL domainURL = domainRepo.findOne("{_id: #}", "www.test.com").as(MongoDomainURL.class);
        assertEquals("www.test.com", domainURL.getAddress());
        assertEquals("test.com", domainURL.getName());
        assertEquals(1000, domainURL.getCoolDownPeriod());
    }

    @Test
    public void testAddSimpleUrls() throws Exception {
        List<String> urls = Arrays.asList("someURL1", "someURL2", "someURL3");
        service.addSimpleUrls(urls, "NEW", "site.com", "origin.com");

        assertEquals(3, urlRepo.count());
        Iterable<MongoSimpleURL> actual = urlRepo.find("{domain: #}", "site.com").as(MongoSimpleURL.class);
        List<String> urlsFound = new ArrayList<>();
        for (MongoSimpleURL crtActual : actual) {
            assertEquals("site.com", crtActual.getDomain());
            urlsFound.add(crtActual.getUrl());
        }
        assertTrue(urlsFound.containsAll(urls));
    }

    @Test
    public void testAddDomainLinks_STRESS() throws Exception {
        List<DomainLink> domainLinks = new ArrayList<>();
        DomainURL domain1 = new DomainURL("d1", "n1");
        for (int i = 0; i < 1000; i++) {
            domainLinks.add(new DomainLink(domain1, new SimpleURL("url"+i, "n"+1, "new")));
        }

        service.addDomainLinks(domainLinks);

        assertEquals(1000, urlRepo.count());
        assertEquals(1, domainRepo.count());
    }

    @Test
    public void testAddDomainLinks() throws Exception {
        List<DomainLink> domainLinks = new ArrayList<>();
        DomainURL domain1 = new DomainURL("d1", "n1");
        domainLinks.add(new DomainLink(domain1, new SimpleURL("url1", "n1", "new")));
        domainLinks.add(new DomainLink(domain1, new SimpleURL("url2", "n2", "new")));
        DomainURL domain2 = new DomainURL("d2", "n2");
        domainLinks.add(new DomainLink(domain2, new SimpleURL("url3", "n1", "new")));
        domainLinks.add(new DomainLink(domain2, new SimpleURL("url4", "n2", "new")));

        service.addDomainLinks(domainLinks);

        assertEquals(4, urlRepo.count());
        assertEquals(2, domainRepo.count());
    }

    @Test
    public void testAddDuplicateDomainLinks() throws Exception {
        List<DomainLink> domainLinks1 = new ArrayList<>();
        DomainURL domain1 = new DomainURL("d1", "n1");
        domainLinks1.add(new DomainLink(domain1, new SimpleURL("url1", "n1", "new")));
        domainLinks1.add(new DomainLink(domain1, new SimpleURL("url2", "n2", "new")));
        DomainURL domain2 = new DomainURL("d2", "n2");
        domainLinks1.add(new DomainLink(domain2, new SimpleURL("url3", "n1", "new")));
        domainLinks1.add(new DomainLink(domain2, new SimpleURL("url4", "n2", "new")));
        List<DomainLink> domainLinks2 = new ArrayList<>();
        DomainURL domain3 = new DomainURL("d3", "n3");
        domainLinks2.add(new DomainLink(domain3, new SimpleURL("url1", "n2", "new")));
        domainLinks2.add(new DomainLink(domain3, new SimpleURL("url5", "n1", "new")));
        domainLinks2.add(new DomainLink(domain2, new SimpleURL("url3", "n2", "new")));
        domainLinks2.add(new DomainLink(domain2, new SimpleURL("url7", "n1", "new")));

        service.addDomainLinks(domainLinks1);
        service.addDomainLinks(domainLinks2);

        assertEquals(6, urlRepo.count());
        assertEquals(3, domainRepo.count());
        assertEquals(3, domainRepo.count());
    }

    @Test
    public void testFindDomains() throws Exception {
        service.addDomainUrl("d1", "url1", 1);
        service.addDomainUrl("d2", "url2", 2);

        List<DomainURL> domains = service.findDomains(new PageRequest(0, 100));

        assertEquals(2, domains.size());
        List<String> urlsFound = new ArrayList<>();
        for (DomainURL domain : domains) {
            urlsFound.add(domain.getAddress());
        }
        assertTrue(urlsFound.containsAll(Arrays.asList("url1", "url2")));
    }

    @Test
    public void testFindURLs() throws Exception {
        List<String> urls = Arrays.asList("someURL1", "someURL2", "someURL3", "someURL4", "someURL5");
        service.addSimpleUrls(urls, "new", "site.com", "origin.com");

        Collection<SimpleURL> found = service.findURLs("site.com", "new", 0, 2);

        assertEquals(2, found.size());
        Iterator<SimpleURL> iterator = found.iterator();
        assertTrue(urls.contains(iterator.next().getUrl()));
        assertTrue(urls.contains(iterator.next().getUrl()));
    }

    @Test
    public void testFindExternalURLs() throws Exception {
        List<String> urls = Arrays.asList("someURL1", "someURL2", "someURL3", "someURL4", "someURL5");
        service.addSimpleUrls(urls, "new", "site.com", "origin.com");

        Collection<SimpleURL> found = service.findExternalURLs("origin.com", "new", 0, 2);

        assertEquals(2, found.size());
        Iterator<SimpleURL> iterator = found.iterator();
        assertTrue(urls.contains(iterator.next().getUrl()));
        assertTrue(urls.contains(iterator.next().getUrl()));
    }

    @Test
    public void testRemoveSimpleUrl() throws Exception {
        List<String> urls = Arrays.asList("someURL1", "someURL2", "someURL3", "someURL4", "someURL5");
        service.addSimpleUrls(urls, "new", "site.com", "origin.com");

        service.removeSimpleUrl("someURL2");

        assertEquals(4, urlRepo.count());
    }

    @Test
    public void testUpdateSimpleUrlsStatus() throws Exception {
        List<String> urls = Arrays.asList("someURL1", "someURL2", "someURL3", "someURL4", "someURL5");
        service.addSimpleUrls(urls, "new", "site.com", "origin.com");

        service.updateSimpleUrlsStatus(Arrays.asList("someURL3", "someURL5"), "old");

        assertEquals(3, urlRepo.count("{status: 'new'}"));
        assertEquals(2, urlRepo.count("{status: 'old'}"));
    }

    @Test
    public void testUpdateSimpleUrlErrorStatus() throws Exception {
        List<String> urls = Arrays.asList("someURL1", "someURL2", "someURL3", "someURL4", "someURL5");
        service.addSimpleUrls(urls, "new", "site.com", "origin.com");

        service.updateSimpleUrlErrorStatus("someURL3", 2);

        assertEquals(4, urlRepo.count("{errorCount: 0}"));
        assertEquals(1, urlRepo.count("{errorCount: 2}"));
        assertEquals(1, urlRepo.count("{status: 'ERROR'}"));
    }

    @Test
    public void testRemoveDomainUrl() throws Exception {
        service.addDomainUrl("site1.com", "site1.com", 2);
        service.addSimpleUrls(Arrays.asList("someURL1a", "someURL1b"), "new", "site1.com", "origin1.com");
        service.addDomainUrl("site2.com", "site2.com", 2);
        service.addSimpleUrls(Arrays.asList("someURL2a", "someURL2b"), "new", "site2.com", "origin2.com");
        assertEquals(2, domainRepo.count());
        assertEquals(4, urlRepo.count());

        service.removeDomainUrl("site1.com");

        assertEquals(1, domainRepo.count());
        assertEquals(2, urlRepo.count());
    }

    @Test
    public void testRemoveDomains() throws Exception {
        service.addDomainUrl("site1.com", "site1.com", 2);
        service.addSimpleUrls(Arrays.asList("someURL1a", "someURL1b"), "new", "site1.com", "origin1.com");
        service.addDomainUrl("site2.com", "site2.com", 2);
        service.addSimpleUrls(Arrays.asList("someURL2a", "someURL2b"), "new", "site2.com", "origin2.com");
        assertEquals(2, domainRepo.count());
        assertEquals(4, urlRepo.count());

        service.removeDomains();

        assertEquals(0, domainRepo.count());
        assertEquals(0, urlRepo.count());
    }
}