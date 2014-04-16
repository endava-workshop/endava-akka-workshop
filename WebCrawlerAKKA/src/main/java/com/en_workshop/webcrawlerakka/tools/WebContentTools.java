package com.en_workshop.webcrawlerakka.tools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Radu Ciumag
 */
public class WebContentTools {
    private static final Logger LOG = LoggerFactory.getLogger(WebContentTools.class);

    /* TODO: Add ALPHA (%41–%5A and %61–%7A) */
    private static final String[] URL_FRAGMENT_REPLACE_SRC = new String[]{"%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37", "%38", "%39",
            "%2d", "%2e", "%5f", "%7e", "/\\.\\./", "/\\./"};
    private static final String[] URL_FRAGMENT_REPLACE_DEST = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "-", ".", "_", "~", "/", "/"};

    private static final String[] URL_FRAGMENT_PATH_REPLACE_SRC = new String[]{"//"};
    private static final String[] URL_FRAGMENT_PATH_REPLACE_DEST = new String[]{"/"};

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
        /* Validate URL link text */
        if (null == urlLink || 0 == urlLink.length()) {
            return urlLink;
        }

        try {
            /* URL as string pre-processing */
            String processedLink = urlLink; //.toLowerCase();

            /* Replace (eliminate) specific link fragments */
            for (int i = 0; i < URL_FRAGMENT_REPLACE_SRC.length; i++) {
                processedLink = processedLink.replaceAll(URL_FRAGMENT_REPLACE_SRC[i], URL_FRAGMENT_REPLACE_DEST[i]);
            }

            URL url = new URL(processedLink);

            final StringBuilder linkBuilder = new StringBuilder();

            /* Protocol */
            if (WebClient.PROTOCOL_HTTPS.equals(url.getProtocol())) {
                linkBuilder.append(WebClient.PROTOCOL_HTTP).append("://");
            } else {
                linkBuilder.append(url.getProtocol()).append("://");
            }

            /* Host */
            String host = url.getHost();
            if (null != host) {
                int dotsCount = 0;
                for (int i = 0; i < host.length(); i++) {
                    if (host.charAt(i) == '.') {
                        dotsCount++;
                    }
                }

                if (dotsCount == 1 && !host.startsWith("www.")) {
                    host = "www." + host;
                }
            }
            linkBuilder.append(host);

            /* Port */
            linkBuilder.append(processURLPort(url.getProtocol(), url.getPort()));

            /* Path */
            String urlPath = url.getPath();
            /* Replace (eliminate) specific path fragments */
            for (int i = 0; i < URL_FRAGMENT_PATH_REPLACE_SRC.length; i++) {
                urlPath = urlPath.replaceAll(URL_FRAGMENT_PATH_REPLACE_SRC[i], URL_FRAGMENT_PATH_REPLACE_DEST[i]);
            }
            linkBuilder.append(urlPath);

            /* Query */
            if (null != url.getQuery()) {
                linkBuilder.append("?").append(url.getQuery());
            }

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

    /**
     * Process the URL port and return the final port fragment to use
     *
     * @param urlProtocol The URL protocol
     * @param urlPort     The URL port
     * @return The final port fragment to use
     */
    private static String processURLPort(final String urlProtocol, final int urlPort) {
        if (-1 == urlPort) {
            return StringUtils.EMPTY;
        }

        /* Skip default ports */
        boolean skipDefaultPort = false;
        if (WebClient.PROTOCOL_HTTP.equals(urlProtocol) && WebClient.PROTOCOL_HTTP_PORT == urlPort) {
            skipDefaultPort = true;
        } else if (WebClient.PROTOCOL_HTTPS.equals(urlProtocol) && WebClient.PROTOCOL_HTTPS_PORT == urlPort) {
            skipDefaultPort = true;
        }

        if (!skipDefaultPort) {
            return ":" + urlPort;
        }

        return StringUtils.EMPTY;
    }

}