/**
 * 
 */
package service.impl.neo4j.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.internal.compiler.v2_0.functions.E;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
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
public class RestNeo4jUrlServiceImpl implements UrlService, Neo4jQueryInterface {

	private RestAPI restApi;
	private QueryEngine engine;

	public RestNeo4jUrlServiceImpl() {
		restApi = new RestAPIFacade("http://localhost:7474/db/data");
		engine = new RestCypherQueryEngine(restApi);
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
		engine.query(CREATE_DOMAIN, paramMap);
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
		QueryResult<Map<String, Object>> result = engine.query(GET_DOMAINS,
				paramMap);
		List<DomainURL> domainList = new ArrayList<DomainURL>();

		Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> row = iterator.next();
			DomainURL domain = new DomainURL(
					(String) row.get("n." + DOMAIN_NAME), (String) row.get("n."
							+ DOMAIN_URL), (Long) row.get("n."
							+ COOL_DOWN_PERIOD));
			domainList.add(domain);
		}
		return domainList;
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#addDomainLinks(java.util.List)
	 */
	@Override
	public void addDomainLinks(List<DomainLink> domainLinks) {
		for(DomainLink link : domainLinks){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(DOMAIN_URL, link.getDomainURL().getAddress());
			paramMap.put(LINK_NAME, link.getSimpleURL().getName());
			paramMap.put(LINK_URL, link.getSimpleURL().getUrl());
			paramMap.put(LINK_STATUS, link.getSimpleURL().getStatus());
			paramMap.put(LINK_LAST_UPDATE, link.getSimpleURL().getLastUpdate());
			paramMap.put(LINK_ERROR_COUNT, link.getSimpleURL().getErrorCount());
			
			engine.query(ADD_DOMAIN_LINK, paramMap);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#updateSimpleUrlsStatus(java.util.List,
	 * java.lang.String)
	 */
	@Override
	public void updateSimpleUrlsStatus(List<String> urls, String status) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see service.UrlService#updateSimpleUrlErrorStatus(java.lang.String, int)
	 */
	@Override
	public void updateSimpleUrlErrorStatus(String url, int errorDelta) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
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

}
