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

import com.akka.ws.neo4j.dao.Neo4jLinkDao;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.enums.LinkStatus;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Neo4jLinkDaoImpl implements Neo4jLinkDao, Neo4jQueryInterface {
	private ReadableIndex nodeIndex;
	private RestAPIFacade restApi;

	private QueryEngine engine;

	public Neo4jLinkDaoImpl(String dbUrl) {
		GraphDatabaseService restGraphDb = new RestGraphDatabase(dbUrl);
		restApi = new RestAPIFacade(dbUrl);
		engine = new RestCypherQueryEngine(restApi);

		AutoIndexer autoIndexer = restGraphDb.index().getNodeAutoIndexer();
		nodeIndex = autoIndexer.getAutoIndex();
	}

	public void addLinksInBatch(final Set<Link> linkSet) {
		// add links
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				for (Link link : linkSet) {
					engine.query(CREATE_LINK, getLinkMap(link));
				}
				return batchResult;
			}
		});
	}

	public boolean addLink(Link link) {
		if (!linkExists(link)) {
			engine.query(CREATE_LINK, getLinkMap(link));
			return true;
		}
		return false;
	}

	public boolean linkExists(Link link) {
		IndexHits hits = nodeIndex.get("lurl", link.getUrl());
		return hits.hasNext();
	}

	@Override
	public List<Link> getDomainLinks(String domainName, String linkStatus, int pageNo, int pageSize) {
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

	public void updateLinks(final List<Link> linkList){
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				for (Link link : linkList) {
					engine.query(UPDATE_LINK, getLinkMap(link));
				}
				return batchResult;
			}
		});
	}
	public long countAllLinks() {
		QueryResult result = engine.query(COUNT_ALL_LINKS, null);
		HashMap map = (HashMap) result.iterator().next();
		Integer count = (Integer) map.values().iterator().next();
		return count;
	}

	public void cleanDatabase() {
		engine.query(REMOVE_ALL_DOMAINS, null);
	}

	private Map<String, Object> getLinkMap(Link link) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(LINK_URL, link.getUrl());
		paramMap.put(LINK_STATUS, link.getStatus().toString());
		return paramMap;
	}

	class BatchResult {
		List<QueryResult> queryResults = new ArrayList<>();
	}

}
