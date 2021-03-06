/**
 * 
 */
package service.impl.neo4j.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.springframework.data.domain.Pageable;

import service.Entries;
import service.UrlService;
import entity.DomainLink;
import entity.DomainURL;
import entity.SimpleURL;

/**
 * @author criss
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RestNeo4jUrlServiceImpl implements UrlService, Neo4jQueryInterface {

	private String dbUrl = "http://localhost:7474/db/data";
	private RestAPIFacade restApi;

	private QueryEngine engine;

	private AutoIndexer<Node> nodeAutoIndexer;

	private RestIndex<Node> index;
	
	public RestNeo4jUrlServiceImpl() {
		restApi = new RestAPIFacade(dbUrl);
		engine = new RestCypherQueryEngine(restApi);

		nodeAutoIndexer = restApi.index().getNodeAutoIndexer();
		
		nodeAutoIndexer.getAutoIndex().get(LINK_URL, "");
		
//		Assert.assertTrue(nodeAutoIndexer.getAutoIndexedProperties().contains("durl"));
//		Assert.assertTrue(nodeAutoIndexer.getAutoIndexedProperties().contains("lurl"));
		
		AutoIndexer<Relationship> relAutoIndexer =  restApi.index().getRelationshipAutoIndexer();
		
		relAutoIndexer.getAutoIndex().get(REL_PART_OF, "");
		
//		RestGraphDatabase restGraphDb = new RestGraphDatabase( dbUrl );
		
		index = restApi.createIndex(Node.class, "unique-node", LuceneIndexImplementation.EXACT_CONFIG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#addDomainUrl(java.lang.String, java.lang.String,
	 * long)
	 */
	@Override
	public DomainURL addDomainUrl(String domainName, String domainUrl,
			long coolDownPeriod) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		paramMap.put(DOMAIN_URL, domainUrl);
		paramMap.put(COOL_DOWN_PERIOD, coolDownPeriod);
		
//		engine.query(CREATE_DOMAIN, paramMap);
		
		RestNode node = restApi.getOrCreateNode(index, "durl", domainUrl, paramMap);
//		if(!node.getLabels().iterator().hasNext()){
//			restApi.addLabels(node, "Domain");
//		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * service.UrlService#findDomains(org.springframework.data.domain.Pageable)
	 */
	@Override
	public List<DomainURL> findDomains(Pageable pageable) {
		if (pageable == null) {
			return null;
		}
		int skip = pageable.getPageNumber() * pageable.getPageSize();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_SKIP, skip);
		paramMap.put(PARAM_LIMIT, pageable.getPageSize());
		QueryResult<Map<String, Object>> result = engine.query(GET_ALL_DOMAINS,
				paramMap);
		List<DomainURL> domainList = new ArrayList<DomainURL>();

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			DomainURL domain = new DomainURL((String) row.get("n."
					+ DOMAIN_NAME), (String) row.get("n." + DOMAIN_URL),
					((Integer) row.get("n." + COOL_DOWN_PERIOD)).longValue());
			domainList.add(domain);
		}
		return domainList;
	}

	public DomainURL findDomain(String domainUrl) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_URL, domainUrl);
		QueryResult<Map<String, Object>> result = engine.query(FIND_DOMAIN,
				paramMap);
		Iterator<Map<String, Object>> iterator = result.iterator();
		if (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			DomainURL domain = new DomainURL((String) row.get("n."
					+ DOMAIN_NAME), (String) row.get("n." + DOMAIN_URL),
					((Integer) row.get("n." + COOL_DOWN_PERIOD)).longValue());
			return domain;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#removeDomainUrl(java.lang.String)
	 */
	@Override
	public void removeDomainUrl(String domainUrl) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_URL, domainUrl);
		engine.query(REMOVE_DOMAIN, paramMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#removeDomains()
	 */
	@Override
	public void removeDomains() {
		engine.query(REMOVE_ALL_DOMAINS, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#addSimpleUrls(java.util.List, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void addSimpleUrls(List<String> urls, String status,
			String domainURL, String sourceDomainName) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#addDomainLinks(java.util.List)
	 */
	@Override
	public void addDomainLinks(List<DomainLink> domainLinks) {
		long time = System.currentTimeMillis();

		Set<String> checkedDomains = new HashSet<>();
		for (DomainLink domainLink : domainLinks) {
			if (!checkedDomains
					.contains(domainLink.getDomainURL().getAddress())) {
				if (findDomain(domainLink.getDomainURL().getAddress()) == null) {
					addDomainUrl(domainLink.getDomainURL().getName(),
							domainLink.getDomainURL().getAddress(), domainLink
									.getDomainURL().getCoolDownPeriod());
				}
				checkedDomains.add(domainLink.getDomainURL().getAddress());
			}
		}

		System.out.println("add domain time : "
				+ (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		addLinksInBatch(domainLinks);
		System.out.println("add links time : "
				+ (System.currentTimeMillis() - time));
		
		time = System.currentTimeMillis();
//		addDomainLinkRelationsInBatch(domainLinks);
//		System.out.println("add relations time : "
//				+ (System.currentTimeMillis() - time));
	}

	private void addLinksInBatch(final List<DomainLink> domainLinks) {
		// add links
		restApi.executeBatch(new BatchCallback<BatchResult>() {
			@Override
			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				QueryEngine engine = new RestCypherQueryEngine(batchRestApi);
				for (DomainLink domainLink : domainLinks) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(LINK_NAME, domainLink.getSimpleURL().getName());
					paramMap.put(LINK_URL, domainLink.getSimpleURL().getUrl());
					paramMap.put(LINK_STATUS, domainLink.getSimpleURL()
							.getStatus());
					paramMap.put(LINK_LAST_UPDATE, domainLink.getSimpleURL()
							.getLastUpdate());
					paramMap.put(LINK_ERROR_COUNT, domainLink.getSimpleURL()
							.getErrorCount());

					engine.query(CREATE_LINK, paramMap);
				}
				return batchResult;
			}
		});
	}

	private void addDomainLinkRelationsInBatch(final List<DomainLink> domainLinks) {
		restApi.executeBatch(new BatchCallback<BatchResult>() {
			@Override
			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				QueryEngine engine = new RestCypherQueryEngine(batchRestApi);
				for (DomainLink domainLink : domainLinks) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(DOMAIN_URL, domainLink.getDomainURL()
							.getAddress());
					paramMap.put(LINK_URL, domainLink.getSimpleURL().getUrl());

					engine.query(CREATE_DOMAIN_LINK_RELATION, paramMap);
				}
				return batchResult;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#updateSimpleUrlsStatus(java.util.List,
	 * java.lang.String)
	 */
	@Override
	public void updateSimpleUrlsStatus(List<String> urls, String status) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(LINK_STATUS, status);
		for (String url : urls) {
			paramMap.put(LINK_URL, url);

			engine.query(UPDATE_LINK_STATUS, paramMap);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#updateSimpleUrlErrorStatus(java.lang.String, int)
	 */
	@Override
	public void updateSimpleUrlErrorStatus(String url, int errorDelta) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(LINK_ERROR_COUNT, errorDelta);
		paramMap.put(LINK_URL, url);

		engine.query(UPDATE_LINK_ERROR_COUNT, paramMap);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#findURLs(java.lang.String, java.lang.String, int,
	 * int)
	 */
	@Override
	public Collection<SimpleURL> findURLs(String address, String status,
			int pageNo, int pageSize) {

		int skip = pageNo * pageSize;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_URL, address);
		paramMap.put(LINK_STATUS, status);
		paramMap.put(PARAM_SKIP, skip);
		paramMap.put(PARAM_LIMIT, pageSize);
		QueryResult<Map<String, Object>> result = engine.query(
				GET_DOMAIN_LINKS, paramMap);
		List<SimpleURL> linksList = new ArrayList<SimpleURL>();

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			SimpleURL domain = new SimpleURL((String) row.get("l." + LINK_URL),
					(String) row.get("l." + LINK_NAME), (String) row.get("l."
							+ LINK_STATUS), (Integer) row.get("l."
							+ LINK_ERROR_COUNT), (Integer) row.get("l."
							+ LINK_LAST_UPDATE));
			linksList.add(domain);
		}
		return linksList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#findExternalURLs(java.lang.String,
	 * java.lang.String, int, int)
	 */
	@Override
	public Collection<SimpleURL> findExternalURLs(String address,
			String status, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#removeSimpleUrl(java.lang.String)
	 */
	@Override
	public void removeSimpleUrl(String simpleUrl) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#query(java.lang.String)
	 */
	@Override
	public List<Entries> query(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countAllNodes() {
		QueryResult result = engine.query(COUNT_ALL_NODES, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	@Override
	public long countAllDomains() {
		QueryResult result = engine.query(COUNT_ALL_DOMAINS, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	@Override
	public long countAllLinks() {
		QueryResult result = engine.query(COUNT_ALL_LINKS, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	@Override
	public long countDomainLinks(String domainUrl) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_URL, domainUrl);
		QueryResult result = engine.query(COUNT_DOMAIN_LINKS, paramMap);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	class BatchResult {
		List<QueryResult> queryResults = new ArrayList<>();
	}
}
