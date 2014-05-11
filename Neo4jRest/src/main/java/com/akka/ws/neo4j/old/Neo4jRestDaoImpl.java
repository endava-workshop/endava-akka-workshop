package com.akka.ws.neo4j.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.DomainLink;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.enums.LinkStatus;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Neo4jRestDaoImpl implements Neo4jQueryInterface {

	private RestAPIFacade restApi;

	private QueryEngine engine;

	private RestIndex<Node> index;

	private boolean addLabels;

	public Neo4jRestDaoImpl(String dbUrl, boolean addLabels) {
		restApi = new RestAPIFacade(dbUrl);
		engine = new RestCypherQueryEngine(restApi);
		this.addLabels = addLabels;
		index = restApi.createIndex(Node.class, "domainIndex", LuceneIndexImplementation.EXACT_CONFIG);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#addDomainUrl(java.lang.String, java.lang.String,
	 * long)
	 */

	public void addDomain(Domain domain) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domain.getName());
		paramMap.put(DOMAIN_STATUS, domain.getDomainStatus().toString());
		paramMap.put(COOL_DOWN_PERIOD, domain.getCoolDownPeriod());
		paramMap.put(CRAWLED_AT, domain.getCrawledAt());

		// RestNode node = restApi.getOrCreateNode(index, DOMAIN_NAME,
		// domain.getName(), paramMap);
		// if (addLabels) {
		// addLabel(node, DOMAIN_LABEL);
		// }
		engine.query(CREATE_DOMAIN, paramMap);
	}

	public void addDomainsInBatch(final List<Domain> domainList) {
		// add links
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				for (Domain domain : domainList) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(DOMAIN_NAME, domain.getName());
					paramMap.put(DOMAIN_STATUS, domain.getDomainStatus().toString());
					paramMap.put(COOL_DOWN_PERIOD, domain.getCoolDownPeriod());
					paramMap.put(CRAWLED_AT, domain.getCrawledAt());

					engine.query(CREATE_DOMAIN, paramMap);
				}
				return batchResult;
			}
		});
	}

	private void addLabel(RestNode node, String label) {
		if (!node.getLabels().iterator().hasNext()) {
			restApi.addLabels(node.labelsPath(), label);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * service.UrlService#getDomains(org.springframework.data.domain.Pageable)
	 */

	public List<Domain> getDomains(int pageNo, int pageSize) {
		int skip = pageNo * pageSize;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_SKIP, skip);
		paramMap.put(PARAM_LIMIT, pageSize);
		QueryResult<Map<String, Object>> result = engine.query(GET_ALL_DOMAINS, paramMap);
		List<Domain> domainList = new ArrayList<Domain>();

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			Domain domain = extractDomain(row);

			domainList.add(domain);
		}
		return domainList;
	}

	public Domain findDomain(String domainName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		QueryResult<Map<String, Object>> result = engine.query(FIND_DOMAIN, paramMap);
		Iterator<Map<String, Object>> iterator = result.iterator();
		if (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			Domain domain = extractDomain(row);
			return domain;
		} else {
			return null;
		}
	}

	public boolean existsDomain(String domainName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		QueryResult<Map<String, Object>> result = engine.query(FIND_DOMAIN, paramMap);
		return result.iterator().hasNext();
	}

	public List<Domain> getNewDomainsFrom(final List<Domain> domainList) {
		List<Domain> newDomainList = new ArrayList<Domain>();
/*
		DomainBatchResult result = restApi.executeBatch(new BatchCallback<DomainBatchResult>() {
			public DomainBatchResult recordBatch(RestAPI batchRestApi) {
				DomainBatchResult batchResult = new DomainBatchResult();
				QueryEngine engine = new RestCypherQueryEngine(batchRestApi);
				for (Domain domain : domainList) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(DOMAIN_NAME, domain.getName());
					QueryResult<Map<String, Object>> result = engine.query(FIND_DOMAIN, paramMap);
					batchResult.resultMap.put(domain, result);
				}
				return batchResult;
			}
		});
		for (Domain domain : result.resultMap.keySet()) {
			if (!result.resultMap.get(domain).iterator().hasNext()) {
				newDomainList.add(domain);
			}
		}
*/
		for (Domain domain : domainList) {
			if (!index.get(DOMAIN_NAME, domain.getName()).hasNext()) {
				newDomainList.add(domain);
			}
		}
		return newDomainList;
	}

	private Domain extractDomain(Map<String, Object> row) {
		return new Domain((String) row.get("n." + DOMAIN_NAME), Long.valueOf((Integer) row.get("n." + COOL_DOWN_PERIOD)),
				Long.valueOf((Integer) row.get("n." + CRAWLED_AT)), DomainStatus.valueOf((String) row.get("n." + DOMAIN_STATUS)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#removeDomainUrl(java.lang.String)
	 */

	public void removeDomainUrl(String domainName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		engine.query(REMOVE_DOMAIN, paramMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#removeDomains()
	 */

	public void removeAllDomains() {
		engine.query(REMOVE_ALL_DOMAINS, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#addDomainLinks(java.util.List)
	 */

	public void addDomainLinks(List<DomainLink> domainLinks) {
		long time = System.currentTimeMillis();

		Set<String> checkedDomains = new HashSet<>();
		for (DomainLink domainLink : domainLinks) {
			String domainName = domainLink.getDomain().getName();
			if (!checkedDomains.contains(domainName)) {
				if (findDomain(domainName) == null) {
					addDomain(domainLink.getDomain());
				}
				checkedDomains.add(domainName);
			}
		}

		System.out.println("add domain time : " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		addLinksInBatch(domainLinks);
		System.out.println("add links time : " + (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();
		// addDomainLinkRelationsInBatch(domainLinks);
		// System.out.println("add relations time : "
		// + (System.currentTimeMillis() - time));
	}

	private void addLinksInBatch(final List<DomainLink> domainLinks) {
		// add links
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				QueryEngine engine = new RestCypherQueryEngine(batchRestApi);
				for (DomainLink domainLink : domainLinks) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(LINK_URL, domainLink.getLink().getUrl());
					paramMap.put(LINK_STATUS, domainLink.getLink().getStatus().toString());

					engine.query(CREATE_LINK, paramMap);
				}
				return batchResult;
			}
		});
	}

	private void addDomainLinkRelationsInBatch(final List<DomainLink> domainLinks) {
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				QueryEngine engine = new RestCypherQueryEngine(batchRestApi);
				for (DomainLink domainLink : domainLinks) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(DOMAIN_NAME, domainLink.getDomain().getName());
					paramMap.put(LINK_URL, domainLink.getLink().getUrl());

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

	public void updateLink(List<Link> linkList) {
		for (Link link : linkList) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(LINK_STATUS, link.getStatus().toString());
			paramMap.put(LINK_URL, link.getUrl());

			engine.query(UPDATE_LINK, paramMap);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#findURLs(java.lang.String, java.lang.String, int,
	 * int)
	 */

	public Collection<Link> findDomainLinks(String domainName, String linkStatus, int pageNo, int pageSize) {

		int skip = pageNo * pageSize;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		paramMap.put(LINK_STATUS, linkStatus);
		paramMap.put(PARAM_SKIP, skip);
		paramMap.put(PARAM_LIMIT, pageSize);
		QueryResult<Map<String, Object>> result = engine.query(GET_DOMAIN_LINKS, paramMap);
		List<Link> linksList = new ArrayList<Link>();

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			Link link = new Link(domainName, (String) row.get("l." + LINK_URL), LinkStatus.valueOf((String) row.get("l." + LINK_STATUS)));
			linksList.add(link);
		}
		return linksList;
	}

	public long countAllNodes() {
		QueryResult result = engine.query(COUNT_ALL_NODES, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	public long countAllDomains() {
		QueryResult result = engine.query(COUNT_ALL_DOMAINS, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	public long countAllLinks() {
		QueryResult result = engine.query(COUNT_ALL_LINKS, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	public long countDomainLinks(String domainName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		QueryResult result = engine.query(COUNT_DOMAIN_LINKS, paramMap);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	class BatchResult {
		List<QueryResult> queryResults = new ArrayList<>();
	}

	class DomainBatchResult {
		Map<Domain, QueryResult> resultMap = new HashMap<Domain, QueryResult>();
	}
}
