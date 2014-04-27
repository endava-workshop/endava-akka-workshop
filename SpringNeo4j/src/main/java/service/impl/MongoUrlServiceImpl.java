package service.impl;

import com.codahale.metrics.Timer;
import entity.DomainLink;
import entity.DomainURL;
import entity.SimpleURL;
import metrics.MetricsConfig;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import service.Entries;
import service.UrlService;
import service.impl.mongo.MongoConfig;
import service.impl.mongo.MongoDomainURL;
import service.impl.mongo.MongoSimpleURL;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("mongoUrlService")
public class MongoUrlServiceImpl implements UrlService {

    @Autowired
    public MetricsConfig metricsConfig;
    @Autowired
    public MongoConfig mongoConfig;

    private Timer domainLinksTimer;
    private Timer updateLinkStatusTimer;
    private Timer updateLinkErrCntTimer;
    private Timer findLinksTimer;
    public MongoCollection domainRepo;
    public MongoCollection urlRepo;


    @PostConstruct
    public void init() {
        domainLinksTimer = metricsConfig.getMetrics().timer(getClass().getName()+"_DOMAIN_LINKS");
        findLinksTimer = metricsConfig.getMetrics().timer(getClass().getName()+"_BULK_FIND_LINK");
        updateLinkStatusTimer = metricsConfig.getMetrics().timer(getClass().getName()+"_LINK_STATUS");
        updateLinkErrCntTimer = metricsConfig.getMetrics().timer(getClass().getName()+"_LINK_ERROR_COUNT");
        if (mongoConfig != null) {
            domainRepo = mongoConfig.getJongo().getCollection("domains");
            urlRepo = mongoConfig.getJongo().getCollection("urls");
            try {
                domainRepo.ensureIndex("{address: 1}");
                urlRepo.ensureIndex("{url: 1}");
                urlRepo.ensureIndex("{domain: 1}");
            } catch (Exception e) {
                System.out.println("Error creating mongo indexes: " + e.getMessage());
            }
        }
    }

    public DomainURL addDomainUrl(String domainName, String domainUrl, long coolDownPeriod) {
        MongoDomainURL old = domainRepo.findOne("{'_id': #}", domainUrl).projection("{_id: 1}").as(MongoDomainURL.class);
        if (old == null) {
            domainRepo.findAndModify("{'_id': #}", domainUrl).upsert().with(
                    "{$set: { name: #, address: #, coolDownPeriod: #}}", domainName, domainUrl, coolDownPeriod).as(MongoDomainURL.class);
        }
		return new DomainURL(domainName, domainUrl, coolDownPeriod);
	}

    @Override
    public void addSimpleUrls(List<String> urls, String status, String domainURL, String sourceDomainName) {
        Iterable<MongoSimpleURL> old;
        Timer.Context time = findLinksTimer.time();
        try {
            old = urlRepo.find("{url: {$in: #}}", urls).projection("{'_id': 0, 'url': 1}").as(MongoSimpleURL.class);
        } finally {
            time.stop();
        }
        for (MongoSimpleURL s : old) {
            urls.remove(s.getUrl());
        }
        List<MongoSimpleURL> objs = new ArrayList<>();
        long dtm = System.currentTimeMillis();
        for (String url : urls) {
            objs.add(new MongoSimpleURL(url, url, domainURL, sourceDomainName, status, 0, dtm));
        }
        urlRepo.insert(objs.toArray());
    }

    @Override
    public void addDomainLinks(List<DomainLink> domainLinks) {
        final Timer.Context context = domainLinksTimer.time();
        try {
            Map<String, DomainURL> domainURLs = new HashMap<>();
            Map<String, Set<String>> simpleURLs = new HashMap<>();
            String status = null;
            for (DomainLink domainLink : domainLinks) {
                String domain = domainLink.getDomainURL().getAddress();
                domainURLs.put(domain, domainLink.getDomainURL());
                Set<String> urls = simpleURLs.get(domain);
                if (urls == null) {
                    urls = new HashSet<>();
                    simpleURLs.put(domain, urls);
                }
                SimpleURL simpleURL = domainLink.getSimpleURL();
                status = simpleURL.getStatus();
                urls.add(simpleURL.getUrl());
                // TODO should we create an entry for source domain?

            }
            for (DomainURL domainURL : domainURLs.values()) {
                addDomainUrl(domainURL.getName(), domainURL.getAddress(), domainURL.getCoolDownPeriod());
            }
            for (Map.Entry<String, Set<String>> entry : simpleURLs.entrySet()) {
                DomainURL domainURL = domainURLs.get(entry.getKey());
                addSimpleUrls(new ArrayList<>(entry.getValue()), status, domainURL.getAddress(), null /*TODO*/);
            }
        } finally {
            context.stop();
        }
    }

    @Override
    public List<DomainURL> findDomains(Pageable pageable) {
        Iterable<MongoDomainURL> domains = domainRepo.find().skip(pageable.getOffset()).limit(pageable.getPageSize()).as(MongoDomainURL.class);
        List<DomainURL> result = new ArrayList<>();
        for (MongoDomainURL mongoDomainURL : domains) {
            result.add(new DomainURL(mongoDomainURL.getName(), mongoDomainURL.getAddress(), mongoDomainURL.getCoolDownPeriod()));
        }
        return result;
    }

    @Override
	public Collection<SimpleURL> findURLs(String address, String status, int pageNo, int pageSize) {
        Iterable<MongoSimpleURL> found = urlRepo.find("{domain: #, status: #}", address, status).skip(pageNo * pageSize).limit(pageSize).as(MongoSimpleURL.class);
		return toSimpleURLs(found);
	}

    @Override
    public Collection<SimpleURL> findExternalURLs(String address, String status, int pageNo, int pageSize) {
        Iterable<MongoSimpleURL> found = urlRepo.find("{sourceDomain: #, status: #}", address, status).skip(pageNo * pageSize).limit(pageSize).as(MongoSimpleURL.class);
        return toSimpleURLs(found);
    }

    private Collection<SimpleURL> toSimpleURLs(Iterable<MongoSimpleURL> found) {
        Collection<SimpleURL> result = new ArrayList<>();
        for (MongoSimpleURL url : found) {
            result.add(new SimpleURL(url.getUrl(), url.getName(), url.getStatus(), url.getErrorCount(), url.getLastUpdate()));
        }
        return result;
    }

	public void removeSimpleUrl(String url) {
        urlRepo.remove("{url: #}", url);
	}

    @Override
    public void updateSimpleUrlsStatus(List<String> urls, String status) {
        Timer.Context time = updateLinkStatusTimer.time();
        try {
            urlRepo.update("{url: {$in: #}}", urls).multi().with("{$set: {status: #}}", status);
        } finally {
            time.stop();
        }
    }

    @Override
    public void updateSimpleUrlErrorStatus(String url, int errorDelta) {
        Timer.Context time = updateLinkErrCntTimer.time();
        try {
            urlRepo.update("{url: #}", url).with("{$inc: {errorCount: #}, $set: {status: 'ERROR'}}", errorDelta);
        } finally {
            time.stop();
        }
    }

    public void removeDomainUrl(String domainURL) {
        urlRepo.remove("{domain: #}", domainURL);
        domainRepo.remove("{address: #}", domainURL);
	}

	@Override
	public void removeDomains() {
        urlRepo.drop();
        domainRepo.drop();
	}


    //  debug purpose: extract data out of neo4j using simple queries
    @Override
    public List<Entries> query(String query) {
       throw new RuntimeException("Not implemented");
    }

}
