package com.en_workshop.webcrawlerakka.tools;

import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author Radu Ciumag
 */
public class WebClient {
    private static final Logger LOG = Logger.getLogger(WebClient.class);

    /**
     * Get the page headers for a specified link
     *
     * @param link The link to access
     * @return The list of {@link org.apache.http.Header}s
     * @throws IOException
     */
    public static synchronized Header[] getPageHeaders(final String link) throws IOException {
        Header[] headers = new Header[0];

        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpHead headRequest = new HttpHead(link);
        try (final CloseableHttpResponse response = httpClient.execute(headRequest)) {
            headers = response.getAllHeaders();
        }

        return headers;
    }

    /**
     * Are the headers mime types accepted
     *
     * @param linkHeaders The page headers
     * @return {@link java.lang.Boolean} {@code true} or {@code false}
     * @throws IOException
     */
    public static synchronized boolean isMediaTypeAccepted(Header[] linkHeaders) throws IOException {
        Boolean mediaAccepted = Boolean.FALSE;

        for (final Header header : linkHeaders) {
            if ("Content-Type".equalsIgnoreCase(header.getName())) {
                final String[] mimeParts = header.getValue().trim().split(";");
                for (final String mimePart : mimeParts) {
                    if (Arrays.binarySearch(WebCrawlerConstants.ACCEPTED_MIME_TYPES, mimePart.trim()) >= 0) {
                        return true;
                    }
                }

                LOG.debug("Mime type " + header.getValue() + " not accepted");
            }
        }

        return false;
    }

    /**
     * Get the page content
     *
     * @param link The page to read
     * @return The page content
     * @throws IOException
     */
    public static synchronized String getPageContent(final String link) throws IOException {
        String content = null;

        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpGet getRequest = new HttpGet(link);

        try (final CloseableHttpResponse response = httpClient.execute(getRequest)) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (final InputStream responseStream = entity.getContent()) {
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));
                    final StringBuilder contentBuilder = new StringBuilder();
                    String line = null;
                    while (null != (line = bufferedReader.readLine())) {
                        contentBuilder.append(line).append('\n');
                    }

                    content = contentBuilder.toString();
                }
            }
        }

        return content;
    }
}
