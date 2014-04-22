package service.impl;

import entity.DomainURL;
import entity.SimpleURL;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;
import org.neo4j.graphdb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import repo.DomainUrlRepo;
import repo.SimpleUrlRepo;
import service.CypherCallback;
import service.Entries;
import service.Entry;
import service.UrlService;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class UrlServiceImpl implements UrlService {

	@Autowired
	DomainUrlRepo domainRepo;
	
	@Autowired
	SimpleUrlRepo simpleUrlRepo;

    @Autowired
    GraphDatabaseService graphDatabaseService;

    private Neo4JHelper neo4JHelper;

    @PostConstruct
    public void init () {
//        try ( Transaction tx = graphDatabaseService.beginTx() )
//        {
//            graphDatabaseService.schema()
//                    .constraintFor( DynamicLabel.label( "SimpleURL" ) )
//                    .assertPropertyIsUnique( "url" )
//                    .create();
//            graphDatabaseService.schema()
//                    .constraintFor( DynamicLabel.label( "DomainURL" ) )
//                    .assertPropertyIsUnique( "address" )
//                    .create();
//            tx.success();
//        }

        neo4JHelper = new Neo4JHelper(graphDatabaseService);
    }

    private CypherCallback urlCallback(final List<SimpleURL> urls, final String alias) {
        return new CypherCallback() {
            @Override
            public void execute(Map<String, Object> props) {
                Node node = (Node) props.get(alias); // query result alias
                urls.add(new SimpleURL(node));
            }
        };
    }

	@Transactional
	public DomainURL addDomainUrl(String domainName, String domainUrl, long coolDownPeriod) {
        long t0 = System.currentTimeMillis();
        DomainURL old = domainRepo.findOneByUrl(domainUrl);
        if (old != null) {
//            System.out.println(domainUrl + " already exists");
            long t1 = System.currentTimeMillis();
            System.out.println("domain checked " + (t1-t0) + "ms");
            return old;
        }
		DomainURL domain = new DomainURL();
        domain.setName(domainName);
        domain.setAddress(domainUrl);
        domain.setCoolDownPeriod(coolDownPeriod);
		domain = domainRepo.save(domain);
        long t1 = System.currentTimeMillis();
        System.out.println("domain added in " + (t1-t0) + "ms");

		return domain;
	}

	@Transactional
    @Override
	public SimpleURL addSimpleUrl(String name, String url, String status, String domainURL, String sourceDomainName) {
        SimpleURL old = simpleUrlRepo.findOneByUrl(url);
        if (old != null) {
//            System.out.println(url + " already exists");
            return old;
        }
        long t0 = System.currentTimeMillis();
        final String query;
        if (StringUtils.hasText(sourceDomainName)) {
            query =
                    "MATCH (ee: DomainURL) " +
                    "MATCH (src: DomainURL) " +
                    "WHERE ee.address={address} and src.address = {sourceAddress} " +
                    "create (u: _SimpleURL: SimpleURL {url: {url}, name: {name}, status: {status}, errorCount: 0, lastUpdate: {lastUpdate}}), \n" +
                    "(ee)-[:CONTAINS]->(u), \n" +
                    "(src)-[:LINKS_TO]->(u) \n" +
                    "RETURN u";
        } else {
            query =
                    "MATCH (ee: DomainURL) WHERE ee.address={address} " +
                    "create (u: _SimpleURL: SimpleURL {url: {url}, name: {name}, status: {status}, errorCount: 0, lastUpdate: {lastUpdate}}), \n" +
                    "(ee)-[:CONTAINS]->(u) \n" +
                    "RETURN u";
        }

        final List<SimpleURL> urls = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("address", domainURL);
        params.put("sourceAddress", sourceDomainName);
        params.put("url", url);
        params.put("status", status);
        params.put("name", String.valueOf(name));
        params.put("lastUpdate", System.currentTimeMillis());
        neo4JHelper.execute(query, params, urlCallback(urls, "u"));

        long t1 = System.currentTimeMillis();
        System.out.println("link added in " + (t1-t0) + "ms");
        return urls.isEmpty() ? null : urls.get(0);
	}

    @Transactional
    @Override
    public void addSimpleUrls(List<String> urls, String status, String domainURL, String sourceDomainName) {
        long t0 = System.currentTimeMillis();
        List<String> toCreate = new ArrayList<>();
        for (String url : urls) {
            if (simpleUrlRepo.findOneByUrl(url) == null) {
                toCreate.add(url);
            }
        }
        long t1 = System.currentTimeMillis();
        if (toCreate.isEmpty()) {
            return;
        }
        StringBuilder buff = new StringBuilder();
        if (StringUtils.hasText(sourceDomainName)) {
            buff.append("MATCH (ee: DomainURL) ").append(
                        "MATCH (src: DomainURL) ").append(
                        "WHERE ee.address='").append(domainURL).append("' and src.address = '").append(sourceDomainName).append("' \n" +
                        "CREATE ");
            int idx = 0;
            for (String url : toCreate) {
                if (idx > 0) {
                    buff.append(", ");
                }
                idx++;
                buff.append(String.format(
                        " (u%d: _SimpleURL: SimpleURL {url: '%s', status: '%s', errorCount: 0, lastUpdate: %d}), (ee)-[:CONTAINS]->(u%d), (src)-[:LINKS_TO]->(u%d) ",
                        idx, url, status, System.currentTimeMillis(), idx, idx));
            }
        } else {
            buff.append("MATCH (ee: DomainURL) WHERE ee.address='").append(domainURL).append("' create ");
            int idx = 0;
            for (String url : toCreate) {
                if (idx > 0) {
                    buff.append(", ");
                }
                idx++;
                buff.append(String.format(
                        "(u%d: _SimpleURL: SimpleURL {url: '%s', status: '%s', errorCount: 0, lastUpdate: %d}), (ee)-[:CONTAINS]->(u%d) ",
                        idx, url, status, System.currentTimeMillis(), idx));
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("address", domainURL);
        params.put("sourceAddress", sourceDomainName);
        List<SimpleURL> tmp = new ArrayList<>();
        long t2 = System.currentTimeMillis();
        neo4JHelper.execute(buff.toString(), params, urlCallback(tmp, "u"));

        long t3 = System.currentTimeMillis();
        System.out.println(urls.size() + " links added in " + (t3-t2) + "ms (" + (t3 - t0) + "ms overall, " + (t1 - t0) + "ms for search)");
    }

    @Transactional
    @Override
    public Page<DomainURL> findDomains(Pageable pageable) {
        Page<DomainURL> domains = domainRepo.findAll(pageable);
        return domains;
    }

    @Transactional
    @Override
	public Collection<SimpleURL> findURLs(String address, String status, int pageNo, int pageSize) {
//        List<SimpleURL> simpleURLs = simpleUrlRepo.find...
        String queryStr = "MATCH (a:DomainURL {address: '%s'})-[:`CONTAINS`]->(url {status: '%s'}) RETURN url SKIP %d LIMIT %d";
        String query = String.format(queryStr, address, status,pageNo, pageSize);
        final List<SimpleURL> urls = new ArrayList<>();
        neo4JHelper.execute(query, null, urlCallback(urls, "url"));
		return urls;
	}

    @Transactional
    @Override
    public Collection<SimpleURL> findExternalURLs(String address, String status, int pageNo, int pageSize) {
        String queryStr = "MATCH (a:DomainURL {address: '%s'})-[:`LINKS_TO`]->(url {status: '%s'}) RETURN url SKIP %d LIMIT %d";
        String query = String.format(queryStr, address, status,pageNo, pageSize);
        final List<SimpleURL> urls = new ArrayList<>();
        neo4JHelper.execute(query, null, urlCallback(urls, "url"));
        return urls;
    }

    @Transactional
	public void removeSimpleUrl(String url) {
        List<SimpleURL> simpleURLs = simpleUrlRepo.findByUrl(url);
        for (SimpleURL simpleURL : simpleURLs) {
			simpleUrlRepo.delete(simpleURL);
		}

	}

    @Override
    public void updateSimpleUrlStatus(String url, String status) {
//        long t0 = System.currentTimeMillis();
        simpleUrlRepo.setUrlStatus(url, status);
//        long t1 = System.currentTimeMillis();
//        System.out.println("status in " + (t1-t0) + "ms");
    }

    @Override
    public void updateSimpleUrlErrorStatus(String url, int errorDelta) {
        simpleUrlRepo.setUrlError(url, errorDelta);
    }

    public void removeDomainUrl(String domainURL) {
		DomainURL domain = domainRepo.findByPropertyValue("address", domainURL);

        if (domain != null) {
            for (SimpleURL simpleURL : domain.getInternalUrlSet()) {
                simpleUrlRepo.delete(simpleURL);
            }

            domainRepo.delete(domain);
        }
	}

	@Transactional
	@Override
	public void removeAllDomains() {
        domainRepo.deleteAll();
		simpleUrlRepo.deleteAll();
//        String query = "MATCH (ee: DomainURL) \n" +
//                "MATCH (u: SimpleURL)" +
//                "DELETE ee, u";
//
//        neo4JHelper.execute(query, null, urlCallback(null, "u"));
	}

//    @Transactional
//    @Override
//    public String query(String query) {
//        final List<Map<String, Object>> raw = new ArrayList<>();
//        CypherCallback c = new CypherCallback() {
//            @Override
//            public void execute(Map<String, Object> props) {
//                raw.add(props);
////                Node node = (Node) props.get(alias); // query result alias
////                urls.add(new SimpleURL(node));
//            }
//        };
//        neo4JHelper.execute(query, null, c);
//        StringBuilder buff = new StringBuilder();
//        for (Map<String, Object> me : raw) {
//            for (Map.Entry<String, Object> entry : me.entrySet()) {
//                buff.append("\n\nkey: ").append(entry.getKey()).append("\nvalue: ");
//                Object valueObj = entry.getValue();
//                if (valueObj instanceof Node) {
//                    Node n = (Node) valueObj;
////                    buff.append("\n\tLabels [");
////                    for (Label label : n.getLabels()) {
////                        buff.append(label.name()).append(" ");
////                    }
////                    buff.append("]");
//                    buff.append("\n\tProperties [");
//                    for (String propKey: n.getPropertyKeys()) {
//                        buff.append("\n\t\t").append(propKey).append("=[");
//                        buff.append(n.getProperty(propKey, "<none>"));
//                        buff.append("]");
//                    }
//                    buff.append("]");
//                } else {
//                    buff.append(valueObj);
//                }
//            }
//        }
//        return buff.toString();
//    }
    @Transactional
    @Override
    public List<Entries> query(String query) {
        final List<Map<String, Object>> raw = new ArrayList<>();
        final List<Entries> result = new ArrayList<>();
        CypherCallback c = new CypherCallback() {
            @Override
            public void execute(Map<String, Object> props) {
                raw.add(props);
            }
        };
        neo4JHelper.execute(query, null, c);
        for (Map<String, Object> me : raw) {
            for (Map.Entry<String, Object> entry : me.entrySet()) {
                Object valueObj = entry.getValue();
                Entries entries = new Entries("", new ArrayList<Entry>());
                result.add(entries);
                if (valueObj instanceof Node) {
                    Node n = (Node) valueObj;
                    for (String propKey: n.getPropertyKeys()) {
                        entries.setK(propKey);
                        entries.getEntries().add(new Entry(propKey, String.valueOf(n.getProperty(propKey, "<none>"))));
                    }
                } else {
                    entries.getEntries().add(new Entry("res", String.valueOf(valueObj)));

                }
            }
        }
        return result;
    }

}
