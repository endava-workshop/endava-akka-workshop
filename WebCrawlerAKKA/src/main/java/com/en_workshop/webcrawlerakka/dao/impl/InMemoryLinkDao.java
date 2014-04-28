package com.en_workshop.webcrawlerakka.dao.impl;

import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.LinkStatus;
import com.sun.java.browser.plugin2.DOM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Radu Ciumag
 */
public class InMemoryLinkDao implements LinkDao {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryLinkDao.class);

    private static final List<Link> LINKS = Collections.synchronizedList(new ArrayList<Link>());


//    /**
//     * Add a {@link com.en_workshop.webcrawlerakka.entities.Link} to the urls list
//     *
//     * @param domain The url domain
//     * @param url    The url
//     * @return The {@link com.en_workshop.webcrawlerakka.entities.Link} added or {@code null}
//     */
//    public synchronized Link add(final Domain domain, final String url) {
//
//        /* Validation */
//        if (null == domain || StringUtils.isBlank(url)) {
//            LOG.error("Cannot create Link with params: " + domain + "; " + url + "; " + url);
//            return null;
//        }
//
////        System.out.println("LinkDao - updated " + domain.getName() + " " + url );
//
//        Link link = new Link(domain.getName(), domain.getName(), url);
//
//        /* Test if the url is already added to the database */
//        if (Link.LINKS.contains(link)) {
//            LOG.error("Link record already found for params: " + domain + "; " + url + "; " + url);
//            return null;
//        }
//
//        Link.LINKS.add(link);
//
//        return link;
//    }

    /**
     * Get the next {@link com.en_workshop.webcrawlerakka.entities.Link} for crawling
     *
     * @param domain The {@link com.en_workshop.webcrawlerakka.entities.Domain} to scan
     * @return The first {@link com.en_workshop.webcrawlerakka.entities.Link} not visited found or {@code null}
     */
    public synchronized Link getNextForCrawling(final Domain domain) {
        /* Validation */
        if (null == domain) {
            LOG.error("Cannot scan a null Domain");
            return null;
        }

        for (Link link : LINKS) {
            if (link.getDomain().equals(domain.getName()) && link.getStatus().equals(LinkStatus.NOT_VISITED)) {
                return link;
            }
        }

        return null;
    }

    /**
     * Update or add a {@link com.en_workshop.webcrawlerakka.entities.Link}
     *
     * @param newDomainLink The new {@link com.en_workshop.webcrawlerakka.entities.Link} to persist
     */
    public synchronized void create(final DomainLink newDomainLink) {
        /* Validation */
        createDomain(newDomainLink.getDomain());
        update(newDomainLink.getLink());
    }

    private void createDomain(Domain newDomain) {
        /* Validation */
        if (null == newDomain) {
            LOG.error("Cannot update a null Domain");
            return;
        }

//        System.out.println("DomainDao - updated " + newDomain.getName());

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

    @Override
    public void update(Link link) {
        /* Validation */
        if (null == link) {
            LOG.error("Cannot update a null Link");
            return;
        }

//        System.out.println("LinkDao - updated " + link);

        /* Remove the old link */
        for (int i = 0; i < LINKS.size(); i++) {
            final Link crtLink = LINKS.get(i);
            if (crtLink.getUrl().equals(link.getUrl())) {
                LINKS.remove(i);
                i--;
            }
        }

        /* Add the new link */
        LINKS.add(link);

    }
}