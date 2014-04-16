package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class DomainDao {
    private static final Logger LOG = LoggerFactory.getLogger(DomainDao.class);

    /**
     * Add a {@link com.en_workshop.webcrawlerakka.entities.Domain} to the collection
     *
     * @param domain The {@link com.en_workshop.webcrawlerakka.entities.Domain} to add
     * @return The {@link com.en_workshop.webcrawlerakka.entities.Domain} addded or {@code null}
     */
    public synchronized static Domain add(final Domain domain) {

        /* Validation */
        if (null == domain) {
            LOG.error("Cannot create a null Domain.");
            return null;
        }

        System.out.println("DomainDao - added " + domain.getName());

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
    public synchronized static Domain find(final String webDomainName) {
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
    public synchronized static List<Domain> findAll() {
        return new ArrayList<>(Domain.DOMAINS);
    }

    /**
     * Update or add a {@link com.en_workshop.webcrawlerakka.entities.Domain}
     *
     * @param newDomain The new {@link com.en_workshop.webcrawlerakka.entities.Domain} to persist
     */
    public synchronized static void update(final Domain newDomain) {
        /* Validation */
        if (null == newDomain) {
            LOG.error("Cannot update a null Domain");
            return;
        }

        System.out.println("DomainDao - updated " + newDomain.getName());

        /* Remove the old domain */
        for (int i = 0; i < Domain.DOMAINS.size(); i++) {
            final Domain crtDomain = Domain.DOMAINS.get(i);
            if (crtDomain.getName().equals(newDomain.getName())) {
                Domain.DOMAINS.remove(i);
                i--;
            }
        }

        /* Add a new domain */
        Domain.DOMAINS.add(newDomain);
    }
}
