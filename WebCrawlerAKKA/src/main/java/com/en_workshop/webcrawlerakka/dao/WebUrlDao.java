package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import com.en_workshop.webcrawlerakka.entities.WebUrlCloner;
import com.en_workshop.webcrawlerakka.enums.WebUrlStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class WebUrlDao {
    private static final Logger LOG = Logger.getLogger(WebUrlDao.class);

    /**
     * Add a {@link com.en_workshop.webcrawlerakka.entities.WebUrl} to the urls list
     *
     * @param domain The url domain
     * @param url       The url
     * @return The {@link com.en_workshop.webcrawlerakka.entities.WebUrl} added or {@code null}
     */
    public static WebUrl add(final Domain domain, final String url) {
        /* Validation */
        if (null == domain || StringUtils.isBlank(url)) {
            LOG.error("Cannot create WebUrl with params: " + domain + "; " + url + "; " + url);
            return null;
        }

        WebUrl webUrl = new WebUrl(domain, url);
        
        /* Test if the url is already added to the database */
        if (WebUrl.URLS.contains(webUrl)) {
            LOG.error("WebUrl record already found for params: " + domain + "; " + url + "; " + url);
            return null;
        }

        WebUrl.URLS.add(webUrl);

        return webUrl;
    }

    /**
     * Get the next {@link com.en_workshop.webcrawlerakka.entities.WebUrl} for crawling
     *
     * @param domain The {@link com.en_workshop.webcrawlerakka.entities.Domain} to scan
     * @return The first {@link com.en_workshop.webcrawlerakka.entities.WebUrl} not visited found or {@code null}
     */
    public static WebUrl getNextForCrawling(final Domain domain) {
        /* Validation */
        if (null == domain) {
            LOG.error("Cannot scan a null WbeDomain");
            return null;
        }

        for (WebUrl webUrl : WebUrl.URLS) {
            if (webUrl.getDomain().equals(domain) && webUrl.getStatus().equals(WebUrlStatus.NOT_VISITED)) {
                return webUrl;
            }
        }

        return null;
    }

    /**
     * Update a {@link com.en_workshop.webcrawlerakka.entities.WebUrl} with a new status
     *
     * @param oldWebUrl The original {@link com.en_workshop.webcrawlerakka.entities.WebUrl}
     * @param newStatus The new status
     * @return The new {@link com.en_workshop.webcrawlerakka.entities.WebUrl} or {@code null}
     */
    public static WebUrl update(final WebUrl oldWebUrl, final WebUrlStatus newStatus) {
        /* Validation */
        if (null == oldWebUrl) {
            LOG.error("Cannot update a null WebUrl");
            return null;
        }

        WebUrl newWebUrl = new WebUrlCloner(oldWebUrl).withStatus(newStatus).build();

        WebUrl.URLS.remove(oldWebUrl);
        WebUrl.URLS.add(newWebUrl);

        return newWebUrl;
    }
}
