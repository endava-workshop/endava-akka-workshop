package com.akka.ws.neo4j.service;

import java.util.List;
import java.util.Set;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.DomainLink;
import com.akka.ws.neo4j.entity.Link;

public interface PersistenceService {
	
	public List<Domain> getDomains(int pageNo, int pageSize);

	public List<Link> getDomainLinks(String domainName, String linkStatus, int pageNo, int pageSize);
	
	public void addDomainLinks(List<DomainLink> domainLinks);
	
	public boolean addDomain(Domain domain);
	
	public void addDomainsInBatch(Set<Domain> domainSet);
	
	public void updateLinks(List<Link> linkList);
	
	public void cleanDatabase();

	public void removeDomain(String domainName);
	
}
