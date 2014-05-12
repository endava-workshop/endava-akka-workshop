package com.akka.ws.neo4j.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import com.akka.ws.neo4j.dao.Neo4jDomainDao;
import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.enums.DomainStatus;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Neo4jDomainDaoImpl implements Neo4jDomainDao, Neo4jQueryInterface {
	private ReadableIndex nodeIndex;
	private RestAPIFacade restApi;

	private QueryEngine engine;

	public Neo4jDomainDaoImpl(String dbUrl) {
		GraphDatabaseService restGraphDb = new RestGraphDatabase(dbUrl);
		restApi = new RestAPIFacade(dbUrl);
		engine = new RestCypherQueryEngine(restApi);

		AutoIndexer autoIndexer = restGraphDb.index().getNodeAutoIndexer();
		nodeIndex = autoIndexer.getAutoIndex();
	}

	public void addDomainsInBatch(final Set<Domain> domainSet) {
		// add links
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				for (Domain domain : domainSet) {
					engine.query(CREATE_DOMAIN, getDomainMap(domain));
				}
				return batchResult;
			}
		});
	}

	public boolean addDomain(Domain domain) {
		if (!domainExists(domain)) {
			engine.query(CREATE_DOMAIN, getDomainMap(domain));
			return true;
		}
		return false;
	}

	public boolean domainExists(Domain domain) {
		IndexHits hits = nodeIndex.get("dname", domain.getName());
		return hits.hasNext();
	}

	@Override
	public List<Domain> getDomains(int pageNo, int pageSize) {
		int skip = pageNo * pageSize;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_SKIP, skip);
		paramMap.put(PARAM_LIMIT, pageSize);
		QueryResult<Map<String, Object>> result = engine.query(GET_ALL_DOMAINS,
				paramMap);
		List<Domain> domainList = new ArrayList<Domain>();

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			Domain domain = extractDomain(row);

			domainList.add(domain);
		}
		return domainList;
	}

	public long countAllDomains() {
		QueryResult result = engine.query(COUNT_ALL_DOMAINS, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	@Override
	public void removeDomain(String domainName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domainName);
		engine.query(REMOVE_DOMAIN, paramMap);
	}

	/**
	 * TODO - this is not working when database contains more than 1 mil nodes
	 * and relations
	 */
	public void cleanDatabase() {
		engine.query(REMOVE_ALL_DOMAINS, null);
	}

	public void addDomainNameConstraints() {
		engine.query(DOMAIN_NAME_CONSTRAINT, null);
	}

	private Map<String, Object> getDomainMap(Domain domain) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(DOMAIN_NAME, domain.getName());
		paramMap.put(DOMAIN_STATUS, domain.getDomainStatus().toString());
		paramMap.put(COOL_DOWN_PERIOD, domain.getCoolDownPeriod());
		paramMap.put(CRAWLED_AT, domain.getCrawledAt());
		return paramMap;
	}

	private Domain extractDomain(Map<String, Object> row) {
		return new Domain((String) row.get("n." + DOMAIN_NAME),
				Long.valueOf((Integer) row.get("n." + COOL_DOWN_PERIOD)),
				Long.valueOf((Integer) row.get("n." + CRAWLED_AT)),
				DomainStatus.valueOf((String) row.get("n." + DOMAIN_STATUS)));
	}

	class BatchResult {
		List<QueryResult> queryResults = new ArrayList<>();
	}

}
