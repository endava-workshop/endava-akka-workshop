package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Radu Ciumag
 */
public class LinkDao {
    private static final Logger LOG = LoggerFactory.getLogger(LinkDao.class);

    /**
     * Add a {@link com.en_workshop.webcrawlerakka.entities.Link} to the urls list
     *
     * @param domain The url domain
     * @param url    The url
     * @return The {@link com.en_workshop.webcrawlerakka.entities.Link} added or {@code null}
     */
    public synchronized static Link add(final Domain domain, final String url) {
        /* Validation */
        if (null == domain || StringUtils.isBlank(url)) {
            LOG.error("Cannot create Link with params: " + domain + "; " + url + "; " + url);
            return null;
        }

        Link link = new Link(domain.getName(), url);
        
        /* Test if the url is already added to the database */
        if (Link.LINKS.contains(link)) {
            LOG.error("Link record already found for params: " + domain + "; " + url + "; " + url);
            return null;
        }

        Link.LINKS.add(link);

        return link;
    }

    /**
     * Get the next {@link com.en_workshop.webcrawlerakka.entities.Link} for crawling
     *
     * @param domain The {@link com.en_workshop.webcrawlerakka.entities.Domain} to scan
     * @return The first {@link com.en_workshop.webcrawlerakka.entities.Link} not visited found or {@code null}
     */
    public synchronized static Link getNextForCrawling(final Domain domain) {
        /* Validation */
        if (null == domain) {
            LOG.error("Cannot scan a null Domain");
            return null;
        }

        for (Link link : Link.LINKS) {
            if (link.getDomain().equals(domain.getName()) && link.getStatus().equals(LinkStatus.NOT_VISITED)) {
                return link;
            }
        }

        return null;
    }

    /**
     * Update or add a {@link com.en_workshop.webcrawlerakka.entities.Link}
     *
     * @param newLink The new {@link com.en_workshop.webcrawlerakka.entities.Link} to persist
     */
    public synchronized static void update(final Link newLink) {
        /* Validation */
        if (null == newLink) {
            LOG.error("Cannot update a null Link");
            return;
        }

        /* Remove the old link */
        for (int i = 0; i < Link.LINKS.size(); i++) {
            final Link crtLink = Link.LINKS.get(i);
            if (crtLink.getUrl().equals(newLink.getUrl())) {
                Link.LINKS.remove(i);
                i--;
            }
        }

        /* Add the new link */
        Link.LINKS.add(newLink);
    }
}
