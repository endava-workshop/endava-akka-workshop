package com.akka.ws.neo4j.util;


public interface Neo4jQueryInterface {

	int DEFAULT_COOLDOWN_PERIOD = 30 * 1000;

	String REL_PART_OF = "PART_OF";
	String REL_LINKS_TO = "LINKS_TO";
	String REL_TYPE = "rtype";

	String DOMAIN_NAME = "dname";
	String DOMAIN_STATUS = "dstatus";
	String DOMAIN_LABEL = "Domain";
	String CRAWLED_AT = "crawledAt";

	String LINK_URL = "lurl";
	String LINK_STATUS = "lstatus";

	String DEST_LINK_URL = "d_lurl";
	String SOURCE_LINK_URL = "s_lurl";

	String LINK_LABEL = "Link";

	String COOL_DOWN_PERIOD = "coolDownPeriod";

	String PARAM_SKIP = "skip";
	String PARAM_LIMIT = "limit";

	/**
	 * ADD queries
	 */
	String CREATE_DOMAIN = "CREATE (n:Domain { " + DOMAIN_NAME + " : {" + DOMAIN_NAME + "}, " + COOL_DOWN_PERIOD + ":{" + COOL_DOWN_PERIOD
			+ "}, " + CRAWLED_AT + ":{" + CRAWLED_AT + "}, " + DOMAIN_STATUS + ":{" + DOMAIN_STATUS + "} })";

	String CREATE_LINK = "CREATE (n:Link { " + LINK_URL + " : {" + LINK_URL + "}, " + LINK_STATUS + ":{" + LINK_STATUS + "} })";

	String CREATE_DOMAIN_LINK_RELATION = "MATCH (d:Domain { " + DOMAIN_NAME + ": {" + DOMAIN_NAME + "} }), (l:Link{" + LINK_URL + " : {"
			+ LINK_URL + "} }) " + "CREATE (d)<-[:" + REL_PART_OF + "{ " + REL_TYPE + " : {" + REL_TYPE + "} }]-(l)";

	String CREATE_LINK_LINK_RELATION = "MATCH (l1:Link { " + LINK_URL + ": {" + SOURCE_LINK_URL + "} }), (l2:Link{" + LINK_URL + " : {"
			+ DEST_LINK_URL + "} }) " + "CREATE (l1)-[:" + REL_LINKS_TO + "{ " + REL_TYPE + " : {" + REL_TYPE + "} }]->(l2)";

	String GET_DOMAIN_LINKS = "MATCH (d:Domain { " + DOMAIN_NAME + ": {" + DOMAIN_NAME + "} })<-[:" + REL_PART_OF + "]-(l:Link{"
			+ LINK_STATUS + ":{" + LINK_STATUS + "} }) RETURN l." + LINK_URL + ", l." + LINK_STATUS + " skip {" + PARAM_SKIP + "} limit {"
			+ PARAM_LIMIT + "}";

	String GET_ALL_DOMAINS = "match (n:Domain) return n." + DOMAIN_NAME + ", n." + DOMAIN_STATUS + ", n." + CRAWLED_AT + ", n."
			+ COOL_DOWN_PERIOD + " skip {" + PARAM_SKIP + "} limit {" + PARAM_LIMIT + "}";

	/**
	 * UPDATE queries
	 */
	String UPDATE_LINK = "match (l:Link{" + LINK_URL + ":{" + LINK_URL + "} }) set l." + LINK_STATUS + " = {" + LINK_STATUS + "}";

	/**
	 * SEARCH queries
	 */

	String FIND_DOMAIN = "MATCH (n:Domain {" + DOMAIN_NAME + ":{" + DOMAIN_NAME + "}}) return n." + DOMAIN_NAME + ", n." + DOMAIN_NAME
			+ ", n." + COOL_DOWN_PERIOD;
	/**
	 * REMOVE queries
	 */
	String REMOVE_DOMAIN = "MATCH (n:Domain{" + DOMAIN_NAME + ":{" + DOMAIN_NAME + "}}) OPTIONAL MATCH (n)<-[r1:" + REL_PART_OF
			+ "]-(t)-[r2:" + REL_LINKS_TO + "]-() DELETE r1,r2,n,t";

	String REMOVE_ALL_DOMAINS = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r";

	/**
	 * add constraints queries
	 */
	String DOMAIN_NAME_CONSTRAINT = "CREATE CONSTRAINT ON (n:Domain) ASSERT n.dname IS UNIQUE";
	
	String LINK_URL_CONSTRAINT = "CREATE CONSTRAINT ON (l:Link) ASSERT l.lurl IS UNIQUE";
	
	/**
	 * COUNT queries
	 */
	String COUNT_ALL_NODES = "MATCH (n) RETURN count(n)";

	String COUNT_ALL_DOMAINS = "MATCH (d:Domain) RETURN count(d)";

	String COUNT_ALL_LINKS = "MATCH (l:Link) RETURN count(l)";

	String COUNT_DOMAIN_LINKS = "MATCH (:Domain{" + DOMAIN_NAME + ":{" + DOMAIN_NAME + "}})<-[:" + REL_PART_OF + "]-(l) RETURN count(l)";

}
