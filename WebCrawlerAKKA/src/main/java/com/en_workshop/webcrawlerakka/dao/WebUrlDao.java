package com.en_workshop.webcrawlerakka.dao;

import com.en_workshop.webcrawlerakka.entities.WebDomain;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
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
     * @param webDomain The url domain
     * @param url       The url
     * @return The {@link com.en_workshop.webcrawlerakka.entities.WebUrl} added or {@code null}
     */
    public static WebUrl add(final WebDomain webDomain, final String url) {
        /* Validation */
        if (null == webDomain || StringUtils.isBlank(url)) {
            LOG.error("Cannot create WebUrl with params: " + webDomain + "; " + url + "; " + url);
            return null;
        }

        //TODO: Normalize url (ref. http://en.wikipedia.org/wiki/URL_normalization)

        WebUrl webUrl = new WebUrl(webDomain, url);
        
        /* Test if the url is already added to the database */
        if (WebUrl.URLS.contains(webUrl)) {
            LOG.error("WebUrl record already found for params: " + webDomain + "; " + url + "; " + url);
            return null;
        }

        WebUrl.URLS.add(webUrl);

        return webUrl;
    }

    /**
     * Get the next {@link com.en_workshop.webcrawlerakka.entities.WebUrl} for crawling
     *
     * @param webDomain The {@link com.en_workshop.webcrawlerakka.entities.WebDomain} to scan
     * @return The first {@link com.en_workshop.webcrawlerakka.entities.WebUrl} not visited found or {@code null}
     */
    public static WebUrl getNextForCrawling(final WebDomain webDomain) {
        /* Validation */
        if (null == webDomain) {
            LOG.error("Cannot scan a null WbeDomain");
            return null;
        }

        for (WebUrl webUrl : WebUrl.URLS) {
            if (webUrl.getWebDomain().equals(webDomain) && webUrl.getStatus().equals(WebUrlStatus.NOT_VISITED)) {
                return webUrl;
            }
        }

        return null;
    }
}
