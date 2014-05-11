package com.akka.ws.neo4j.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akka.ws.neo4j.dao.Neo4jRelationDao;
import com.akka.ws.neo4j.entity.Relation;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

public class Neo4jRelationDaoImpl implements Neo4jRelationDao, Neo4jQueryInterface {

	private static Logger logger = LoggerFactory.getLogger(Neo4jRelationDaoImpl.class);

	private ReadableIndex relationIndex;
	private RestAPIFacade restApi;

	private QueryEngine engine;

	public Neo4jRelationDaoImpl(String dbUrl) {
		GraphDatabaseService restGraphDb = new RestGraphDatabase(dbUrl);
		restApi = new RestAPIFacade(dbUrl);
		engine = new RestCypherQueryEngine(restApi);

		AutoIndexer autoIndexer = restGraphDb.index().getRelationshipAutoIndexer();
		relationIndex = autoIndexer.getAutoIndex();
	}

	@Override
	public void addRelationsInBatch(final Set<Relation> relationSet) {
		restApi.executeBatch(new BatchCallback<BatchResult>() {

			public BatchResult recordBatch(RestAPI batchRestApi) {
				BatchResult batchResult = new BatchResult();
				for (Relation relation : relationSet) {
					addRelation(relation);
				}
				return batchResult;
			}
		});
	}

	@Override
	public boolean addRelation(Relation relation) {
		if (relation.getRelationType().equals(REL_PART_OF)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(DOMAIN_NAME, relation.getDestName());
			paramMap.put(LINK_URL, relation.getSourceName());
			paramMap.put(REL_TYPE, relation.getRelationType() + relation.getSourceName() + relation.getDestName());
			
			engine.query(CREATE_DOMAIN_LINK_RELATION, paramMap);
			return true;
		} else if (relation.getRelationType().equals(REL_LINKS_TO)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(DEST_LINK_URL, relation.getDestName());
			paramMap.put(SOURCE_LINK_URL, relation.getSourceName());
			paramMap.put(REL_TYPE, relation.getRelationType() + relation.getSourceName() + relation.getDestName());
			engine.query(CREATE_LINK_LINK_RELATION, paramMap);
			return true;
		} else {
			logger.warn("Unknown type of the relation :" + relation.toString());
			return false;
		}
	}

	@Override
	public boolean relationExists(Relation relation) {
		IndexHits hits = relationIndex.get(REL_TYPE, relation.getRelationType() + relation.getSourceName() + relation.getDestName());
		return hits.hasNext();
	}

	@Override
	public long countAllRelations(String relationType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void cleanDatabase() {
		engine.query(REMOVE_ALL_DOMAINS, null);
	}

	class BatchResult {
		List<QueryResult> queryResults = new ArrayList<>();
	}
}
