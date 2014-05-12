package com.akka.ws.neo4j.dao;

import java.util.List;
import java.util.Set;

import com.akka.ws.neo4j.entity.Link;

public interface Neo4jLinkDao {

	public void addLinksInBatch(Set<Link> linkSet);

	public List<Link> getDomainLinks(String domainName, String linkStatus, int pageNo, int pageSize);

	public boolean addLink(Link link);

	public boolean linkExists(Link link);

	public long countAllLinks();
	
	public void cleanDatabase();

	public void updateLinks(List<Link> linkList);

	public void addLinkUrlConstraints();
}
