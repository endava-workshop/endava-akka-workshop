package com.akka.ws.neo4j.dao;

import java.util.List;
import java.util.Set;

import com.akka.ws.neo4j.entity.Domain;

public interface Neo4jDomainDao {

	public void addDomainsInBatch(Set<Domain> domainSet);

	public List<Domain> getDomains(int pageNo, int pageSize);
	
	public void cleanDatabase();
	
	public boolean addDomain(Domain domain);

	public boolean domainExists(Domain domain);
	
	public long countAllDomains();

	public void removeDomain(String domainName);

}
