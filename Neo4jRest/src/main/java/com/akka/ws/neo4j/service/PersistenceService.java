package com.akka.ws.neo4j.service;

import java.util.Collection;
import java.util.List;

import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.DomainLink;
import com.akka.ws.neo4j.entity.Link;

public interface PersistenceService {
	public void addDomain(Domain domain);

	public void addDomainsInBatch(final List<Domain> domainList);

	public List<Domain> findDomains(int pageNo, int pageSize);

	public Domain findDomain(String domainName);

	public void removeDomainUrl(String domainName);

	public void removeDomains();

	public void addDomainLinks(List<DomainLink> domainLinks);

	public void updateLink(List<Link> linkList);

	public Collection<Link> findDomainLinks(String domainName, String linkStatus, int pageNo, int pageSize);

	public long countAllNodes();

	public long countAllDomains();

	public long countAllLinks();

	public long countDomainLinks(String domainName);
}
