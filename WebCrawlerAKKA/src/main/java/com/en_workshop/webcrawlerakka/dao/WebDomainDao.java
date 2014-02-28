package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.WebDomain;
import com.en_workshop.webcrawlerakka.entities.WebDomainCloner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Radu Ciumag
 */
public class WebDomainDao {
    private static final Logger LOG = Logger.getLogger(WebDomainDao.class);

    /**
     * Add a {@link com.en_workshop.webcrawlerakka.entities.WebDomain} to the collection
     *
     * @param webDomain The {@link com.en_workshop.webcrawlerakka.entities.WebDomain} to add
     * @return The {@link com.en_workshop.webcrawlerakka.entities.WebDomain} addded or {@code null}
     */
    public static WebDomain add(final WebDomain webDomain) {
        /* Validation */
        if (null == webDomain) {
            LOG.error("Cannot create a null WebDomain.");
            return null;
        }

        /* Test if the domain is already added to the database */
        if (WebDomain.DOMAINS.contains(webDomain)) {
            LOG.error("WebDomain record already found for info: " + webDomain);
            return null;
        }

        WebDomain.DOMAINS.add(webDomain);

        return webDomain;
    }

    /**
     * Find a {@link com.en_workshop.webcrawlerakka.entities.WebDomain} based on the domain's base url
     *
     * @param webDomainUrl The web domain's base url
     * @return The {@link com.en_workshop.webcrawlerakka.entities.WebDomain} found or {@code null}
     */
    public static WebDomain find(final String webDomainUrl) {
        if (StringUtils.isBlank(webDomainUrl)) {
            return null;
        }

        for (WebDomain webDomain : WebDomain.DOMAINS) {
            if (webDomain.getBaseUrl().equalsIgnoreCase(webDomainUrl)) {
                return webDomain;
            }
        }

        return null;
    }

    /**
     * Find all {@link com.en_workshop.webcrawlerakka.entities.WebDomain}s
     *
     * @return The list of {@link com.en_workshop.webcrawlerakka.entities.WebDomain}s found
     */
    public static List<WebDomain> findAll() {
        return WebDomain.DOMAINS;
    }

    /**
     * Update a {@link com.en_workshop.webcrawlerakka.entities.WebDomain} with a new crawled time
     *
     * @param oldWebDomain The original {@link com.en_workshop.webcrawlerakka.entities.WebDomain}
     * @param crawledAt    The new "crawled at" time
     * @return The new {@link com.en_workshop.webcrawlerakka.entities.WebDomain} or {@code null}
     */
    public static WebDomain update(final WebDomain oldWebDomain, final long crawledAt) {
        /* Validation */
        if (null == oldWebDomain) {
            LOG.error("Cannot update a null WebDomain");
            return null;
        }

        WebDomain newWebDomain = new WebDomainCloner(oldWebDomain).withCrawledAt(crawledAt).build();

        WebDomain.DOMAINS.remove(oldWebDomain);
        WebDomain.DOMAINS.add(newWebDomain);

        return newWebDomain;
    }
}
