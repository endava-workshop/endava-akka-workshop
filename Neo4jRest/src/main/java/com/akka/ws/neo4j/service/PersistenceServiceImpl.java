package com.akka.ws.neo4j.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akka.ws.neo4j.dao.Neo4jDomainDao;
import com.akka.ws.neo4j.dao.Neo4jLinkDao;
import com.akka.ws.neo4j.dao.Neo4jRelationDao;
import com.akka.ws.neo4j.dao.impl.Neo4jDomainDaoImpl;
import com.akka.ws.neo4j.dao.impl.Neo4jLinkDaoImpl;
import com.akka.ws.neo4j.dao.impl.Neo4jRelationDaoImpl;
import com.akka.ws.neo4j.entity.Domain;
import com.akka.ws.neo4j.entity.DomainLink;
import com.akka.ws.neo4j.entity.Link;
import com.akka.ws.neo4j.entity.Relation;
import com.akka.ws.neo4j.util.Neo4jQueryInterface;

public class PersistenceServiceImpl implements PersistenceService, Neo4jQueryInterface {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceServiceImpl.class);

	Neo4jDomainDao neo4jDomainDao;

	Neo4jLinkDao neo4jLinkDao;

	Neo4jRelationDao neo4jRelationDao;

	String dbUrl = "http://localhost:7474/db/data";

	private boolean groupDataInBatch;

	Set<Domain> newDomainSet = new HashSet<Domain>();
	Set<Link> newLinkSet = new HashSet<Link>();
	Set<Relation> newRelationSet = new HashSet<Relation>();

	private int batchMinSize;

	public PersistenceServiceImpl() {
		neo4jDomainDao = new Neo4jDomainDaoImpl(dbUrl);
		neo4jLinkDao = new Neo4jLinkDaoImpl(dbUrl);
		neo4jRelationDao = new Neo4jRelationDaoImpl(dbUrl);
		this.groupDataInBatch = false;
	}

	public PersistenceServiceImpl(boolean groupDataInBatch, int batchMinSize) {
		neo4jDomainDao = new Neo4jDomainDaoImpl(dbUrl);
		neo4jLinkDao = new Neo4jLinkDaoImpl(dbUrl);
		neo4jRelationDao = new Neo4jRelationDaoImpl(dbUrl);
		this.groupDataInBatch = groupDataInBatch;
		if (this.groupDataInBatch) {
			this.batchMinSize = batchMinSize;
		}
	}

	@Override
	public List<Domain> getDomains(int pageNo, int pageSize) {
		return neo4jDomainDao.getDomains(pageNo, pageSize);
	}

	public List<Link> getDomainLinks(String domainName, String linkStatus, int pageNo, int pageSize) {
		return neo4jLinkDao.getDomainLinks(domainName, linkStatus, pageNo, pageSize);
	}
	
	@Override
	public void addDomainLinks(List<DomainLink> domainLinkList) {
		// logger.debug("received " + domainLinkList.size() +
		// " domain links for storing");
		Set<Domain> domainSet = new HashSet<Domain>();
		Set<Link> linkSet = new HashSet<Link>();
		Set<Relation> relationSet = new HashSet<Relation>();

		int cachedDomains = newDomainSet.size();
		int cachedLinks = newLinkSet.size();
		int cachedRelations = newRelationSet.size();

		for (DomainLink domainLink : domainLinkList) {
			// scanned domain, it should be in the database
			Domain domain = domainLink.getDomain();

			Link link = domainLink.getLink();
			linkSet.add(link);
			if (link.getDomain().equals(domain.getName())) {
				// is part of this domain
				relationSet.add(new Relation(link.getUrl(), domain.getName(), REL_PART_OF));
				if (!link.getSourceLink().equals(link.getDomain())) {
					/**
					 * TODO - not sure if makes sense to set the relations
					 * between links from the same domain
					 */
//					relationSet.add(new Relation(link.getSourceLink(), link.getUrl(), REL_LINKS_TO));
				}
			} else {
				// add new domain
				domainSet.add(new Domain(link.getDomain(), DEFAULT_COOLDOWN_PERIOD, 0));
				// add relation from Link to domain
				relationSet.add(new Relation(link.getUrl(), link.getDomain(), REL_PART_OF));
				// add relation between links
				relationSet.add(new Relation(link.getSourceLink(), link.getUrl(), REL_LINKS_TO));
			}
		}
		// remove existing entries
		// domains
		for (Domain domain : domainSet) {
			if (!neo4jDomainDao.domainExists(domain)) {
				newDomainSet.add(domain);
			}
		}
		// logger.debug((newDomainSet.size() - cachedDomains) +
		// " new domains from a total of " + domainSet.size());
		// links
		for (Link link : linkSet) {
			if (!neo4jLinkDao.linkExists(link)) {
				newLinkSet.add(link);
			}
		}
		// logger.debug((newLinkSet.size() - cachedLinks) +
		// " new links from a total of " + linkSet.size());
		// relations
		for (Relation relation : relationSet) {
			if (!neo4jRelationDao.relationExists(relation)) {
				newRelationSet.add(relation);
			}
		}
		// logger.debug((newRelationSet.size() - cachedRelations) +
		// " new relations from a total of " + relationSet.size());

		if (groupDataInBatch) {
			int totalNewData = newDomainSet.size() + newLinkSet.size() + newRelationSet.size();
			if (totalNewData > batchMinSize) {
				long time = System.currentTimeMillis();
				logger.debug("persist " + (newDomainSet.size() + newLinkSet.size()) + " new nodes and " + newRelationSet.size()
						+ " relations");
				persistData();
				logger.debug("persisted all in " + (System.currentTimeMillis() - time) + " ms");
			}
		} else {
			neo4jDomainDao.addDomainsInBatch(newDomainSet);
			neo4jLinkDao.addLinksInBatch(newLinkSet);
			neo4jRelationDao.addRelationsInBatch(newRelationSet);
		}

	}

	public void updateLinks(List<Link> linkList){
		neo4jLinkDao.updateLinks(linkList);
	}
	
	private void persistData() {
		neo4jDomainDao.addDomainsInBatch(newDomainSet);
		neo4jLinkDao.addLinksInBatch(newLinkSet);
		neo4jRelationDao.addRelationsInBatch(newRelationSet);
		newDomainSet.clear();
		newLinkSet.clear();
		newRelationSet.clear();
	}

	public boolean addDomain(Domain domain) {
		return neo4jDomainDao.addDomain(domain);
	}

	public void addDomainsInBatch(Set<Domain> domainSet) {
		neo4jDomainDao.addDomainsInBatch(domainSet);
	}

	@Override
	public void cleanDatabase() {
		neo4jDomainDao.cleanDatabase();
	}

	public void removeDomain(String domainName) {
		neo4jDomainDao.removeDomain(domainName);
	}

}
