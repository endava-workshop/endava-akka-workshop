package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainCloner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Radu Ciumag
 */
public class DomainDao {
    private static final Logger LOG = Logger.getLogger(DomainDao.class);

    /**
     * Add a {@link com.en_workshop.webcrawlerakka.entities.Domain} to the collection
     *
     * @param domain The {@link com.en_workshop.webcrawlerakka.entities.Domain} to add
     * @return The {@link com.en_workshop.webcrawlerakka.entities.Domain} addded or {@code null}
     */
    public static Domain add(final Domain domain) {
        /* Validation */
        if (null == domain) {
            LOG.error("Cannot create a null Domain.");
            return null;
        }

        /* Test if the domain is already added to the database */
        if (Domain.DOMAINS.contains(domain)) {
            LOG.error("Domain record already found for info: " + domain);
            return null;
        }

        Domain.DOMAINS.add(domain);

        return domain;
    }

    /**
     * Find a {@link com.en_workshop.webcrawlerakka.entities.Domain} based on the domain's base url
     *
     * @param webDomainName The web domain's name.
     * @return The {@link com.en_workshop.webcrawlerakka.entities.Domain} found or {@code null}
     */
    public static Domain find(final String webDomainName) {
        if (StringUtils.isBlank(webDomainName)) {
            return null;
        }

        for (Domain domain : Domain.DOMAINS) {
            if (domain.getName().equalsIgnoreCase(webDomainName)) {
                return domain;
            }
        }

        return null;
    }

    /**
     * Find all {@link com.en_workshop.webcrawlerakka.entities.Domain}s
     *
     * @return The list of {@link com.en_workshop.webcrawlerakka.entities.Domain}s found
     */
    public static List<Domain> findAll() {
        return Domain.DOMAINS;
    }

    /**
     * Update a {@link com.en_workshop.webcrawlerakka.entities.Domain} with a new crawled time
     *
     * @param oldDomain The original {@link com.en_workshop.webcrawlerakka.entities.Domain}
     * @param crawledAt    The new "crawled at" time
     * @return The new {@link com.en_workshop.webcrawlerakka.entities.Domain} or {@code null}
     */
    public static Domain update(final Domain oldDomain, final long crawledAt) {
        /* Validation */
        if (null == oldDomain) {
            LOG.error("Cannot update a null Domain");
            return null;
        }

        Domain newDomain = new DomainCloner(oldDomain).withCrawledAt(crawledAt).build();

        Domain.DOMAINS.remove(oldDomain);
        Domain.DOMAINS.add(newDomain);

        return newDomain;
    }
}
