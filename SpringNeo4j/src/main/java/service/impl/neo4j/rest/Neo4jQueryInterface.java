package service.impl.neo4j.rest;

public interface Neo4jQueryInterface {

	String REL_PART_OF = "PART_OF";
	String REL_LINKS_TO = "LINKS_TO";

	String LINK_NAME = "lname";
	String LINK_URL = "lurl";
	String COOL_DOWN_PERIOD = "coolDownPeriod";

	String DOMAIN_URL = "durl";
	String DOMAIN_NAME = "dname";

	String PARAM_SKIP = "skip";
	String PARAM_LIMIT = "limit";

	String LINK_STATUS = "status";
	String LINK_LAST_UPDATE = "lastUpdate";
	String LINK_ERROR_COUNT = "errorCount";

	/**
	 * ADD queries
	 */
	String CREATE_DOMAIN = "CREATE (n:Domain { " + DOMAIN_NAME + " : {"
			+ DOMAIN_NAME + "}, " + DOMAIN_URL + ":{" + DOMAIN_URL + "}, "
			+ COOL_DOWN_PERIOD + ":{" + COOL_DOWN_PERIOD + "} })";

	String GET_ALL_DOMAINS = "match (n:Domain) return n." + DOMAIN_NAME
			+ ", n." + DOMAIN_URL + ", n." + COOL_DOWN_PERIOD + " skip {"
			+ PARAM_SKIP + "} limit {" + PARAM_LIMIT + "}";

	String ADD_DOMAIN_LINK = "MATCH (d:Domain { " + DOMAIN_URL + ": {"
			+ DOMAIN_URL + "} }) CREATE UNIQUE (d)<-[:" + REL_PART_OF
			+ "]-(l:Link { " + LINK_NAME + ":{" + LINK_NAME + "}, " + LINK_URL
			+ ":{" + LINK_URL + "}, " + LINK_STATUS + ":{" + LINK_STATUS
			+ "}, " + LINK_LAST_UPDATE + ":{" + LINK_LAST_UPDATE + "}, "
			+ LINK_ERROR_COUNT + ":{" + LINK_ERROR_COUNT + "} })";

	String GET_DOMAIN_LINKS = "MATCH (d:Domain { " + DOMAIN_URL + ": {"
			+ DOMAIN_URL + "} })<-[:" + REL_PART_OF + "]-(l:Link{"
			+ LINK_STATUS + ":{" + LINK_STATUS + "} }) RETURN l." + LINK_URL
			+ ", l." + LINK_NAME + ", l." + LINK_STATUS + ", l."
			+ LINK_LAST_UPDATE + ", l." + LINK_ERROR_COUNT + " skip {"
			+ PARAM_SKIP + "} limit {" + PARAM_LIMIT + "}";

	/**
	 * UPDATE queries
	 */
	String UPDATE_LINK_STATUS = "match (l:Link{" + LINK_URL + ":{" + LINK_URL
			+ "} }) set l." + LINK_STATUS + " = {" + LINK_STATUS + "}";

	String UPDATE_LINK_ERROR_COUNT = "match (l:Link{" + LINK_URL + ":{"
			+ LINK_URL + "} }) set l." + LINK_ERROR_COUNT + " = {"
			+ LINK_ERROR_COUNT + "}";

	/**
	 * SEARCH queries
	 */

	String FIND_DOMAIN = "MATCH (n:Domain {" + DOMAIN_URL + ":{" + DOMAIN_URL
			+ "}}) return n." + DOMAIN_NAME + ", n." + DOMAIN_URL + ", n."
			+ COOL_DOWN_PERIOD;
	/**
	 * REMOVE queries
	 */
	String REMOVE_DOMAIN = "MATCH (n:Domain{" + DOMAIN_URL + ":{" + DOMAIN_URL
			+ "}}) OPTIONAL MATCH (n)<-[r1:" + REL_PART_OF + "]-(t)-[r2:"
			+ REL_LINKS_TO + "]-() DELETE r1,r2,n,t";

	String REMOVE_ALL_DOMAINS = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r";

	/**
	 * COUNT queries
	 */
	String COUNT_ALL_NODES = "MATCH (n) RETURN count(n)";

	String COUNT_ALL_DOMAINS = "MATCH (d:Domain) RETURN count(d)";

	String COUNT_ALL_LINKS = "MATCH (l:Link) RETURN count(l)";

	String COUNT_DOMAIN_LINKS = "MATCH (:Domain{" + DOMAIN_URL + ":{"
			+ DOMAIN_URL + "}})<-[:" + REL_PART_OF + "]-(l) RETURN count(l)";

}
