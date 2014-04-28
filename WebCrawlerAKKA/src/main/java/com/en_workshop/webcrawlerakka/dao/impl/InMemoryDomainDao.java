package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.enums.DomainStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class InMemoryDomainDao implements DomainDao {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryDomainDao.class);

    public static final List<Domain> DOMAINS = Collections.synchronizedList(new ArrayList<Domain>());
    public static final Object domainsLock = new Object();

//    /**
//     * Find a {@link com.en_workshop.webcrawlerakka.entities.Domain} based on the domain's base url
//     *
//     * @param webDomainName The web domain's name.
//     * @return The {@link com.en_workshop.webcrawlerakka.entities.Domain} found or {@code null}
//     */
//    public synchronized static Domain find(final String webDomainName) {
//        if (StringUtils.isBlank(webDomainName)) {
//            return null;
//        }
//
//        for (Domain domain : Domain.DOMAINS) {
//            if (domain.getName().equalsIgnoreCase(webDomainName)) {
//                return domain;
//            }
//        }
//
//        return null;
//    }

    /**
     * Find all {@link com.en_workshop.webcrawlerakka.entities.Domain}s
     *
     * @return The list of {@link com.en_workshop.webcrawlerakka.entities.Domain}s found
     */
    public synchronized List<Domain> findAll() {
        return new ArrayList<>(DOMAINS);
    }

    @Override
    public List<Domain> findAll(List<DomainStatus> domainStatuses) {
        List<Domain> filteredDomain = new ArrayList<>();
        synchronized (domainsLock) {
            for (Domain domain : DOMAINS) {
                if (domainStatuses.contains(domain.getDomainStatus())) {
                    filteredDomain.add(domain);
                }
            }
        }
        return filteredDomain;
    }

    /**
     * Update or add a {@link com.en_workshop.webcrawlerakka.entities.Domain}
     *
     * @param newDomain The new {@link com.en_workshop.webcrawlerakka.entities.Domain} to persist
     */
    public void update(final Domain newDomain) {
        /* Validation */
        if (null == newDomain) {
            LOG.error("Cannot update a null Domain");
            return;
        }

        synchronized (domainsLock) {
        /* Remove the old domain */
            for (int i = 0; i < DOMAINS.size(); i++) {
                final Domain crtDomain = DOMAINS.get(i);
                if (crtDomain.getName().equals(newDomain.getName())) {
                    DOMAINS.remove(i);
                    i--;
                }
            }
            /* Add a new domain */
            DOMAINS.add(newDomain);
        }
    }

}
