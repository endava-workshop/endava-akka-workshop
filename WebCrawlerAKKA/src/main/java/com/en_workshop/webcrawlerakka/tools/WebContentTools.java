package com.en_workshop.webcrawlerakka.tools;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Radu Ciumag
 */
public class WebContentTools {
    private static final Logger LOG = Logger.getLogger(WebContentTools.class);

    /**
     * Normalize the URL. URLs that are mall formed will not be processed.
     * <p/>
     * Refs:
     * URL normalization - http://en.wikipedia.org/wiki/URL_normalization
     * Do Not Crawl in the DUST - http://www2007.org/papers/paper194.pdf
     *
     * @param urlLink The URL link to be normalized
     * @return The normalized link as {@link java.lang.String}
     */
    public static String normalizeURLLink(final String urlLink) {
        try {
            URL url = new URL(urlLink);

            StringBuilder linkBuilder = new StringBuilder();


            System.out.println("protocol = " + aURL.getProtocol());
            System.out.println("authority = " + aURL.getAuthority());
            System.out.println("host = " + aURL.getHost());
            System.out.println("port = " + aURL.getPort());
            System.out.println("path = " + aURL.getPath());
            System.out.println("query = " + aURL.getQuery());
            System.out.println("filename = " + aURL.getFile());
            System.out.println("ref = " + aURL.getRef());


            return linkBuilder.toString();
        } catch (MalformedURLException exc) {
            LOG.error("Will not normalize URL: " + urlLink + " - " + exc.getMessage());
        }

        return urlLink;
    }

    /**
     * Normalize the batch of URL links
     *
     * @param urlLinks The collection of URL links
     * @return The collection of links (as {@link java.lang.String}s) normalized
     */
    public static String[] normalizeURLLinks(final String[] urlLinks) {
        final String[] results = new String[urlLinks.length];

        /* Normalize each link in the collection */
        for (int i = 0; i < results.length; i++) {
            results[i] = normalizeURLLink(urlLinks[i]);
        }

        return results;
    }
}