package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.rest.DomainURLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ionut on 20.04.2014.
 */
public class RestDomainDao implements DomainDao {
    private static final Logger LOG = LoggerFactory.getLogger(RestDomainDao.class);

    private Set<String> knownDomains = Collections.synchronizedSet(new HashSet<String>());

    public void add(final Domain domain) {
        /* Validation */
        if (null == domain) {
            LOG.error("Cannot create a null Domain.");
            return;
        }

        String domainName = domain.getName();
        if (knownDomains.contains(domainName)) {
            return;
        }

        DomainURLClient.addDomain(domainName, domainName, domain.getCoolDownPeriod());
        knownDomains.add(domainName);
    }

    public List<Domain> findAll() {
        return DomainURLClient.listDomains(0, 1000);
    }

}
