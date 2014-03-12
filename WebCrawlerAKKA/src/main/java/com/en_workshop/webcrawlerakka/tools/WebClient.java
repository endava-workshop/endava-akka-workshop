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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Radu Ciumag
 */
public class WebClient {
    private static final Logger LOG = Logger.getLogger(WebClient.class);

    public static final String PROTOCOL_HTTP = "http";
    public static final int PROTOCOL_HTTP_PORT = 80;
    public static final String PROTOCOL_HTTPS = "https";
    public static final int PROTOCOL_HTTPS_PORT = 443;

    //TODO Set and use User-Agent

    /**
     * Get the page headers for a specified link
     *
     * @param link The link to access
     * @return The list of {@link org.apache.http.Header}s
     */
    public static synchronized Map<String, String> getPageHeaders(final String link) {
        final Map<String, String> headersMap = new HashMap<>();

        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpHead headRequest = new HttpHead(link);
        try (final CloseableHttpResponse response = httpClient.execute(headRequest)) {
            /* Add page headers */
            final Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                headersMap.put(header.getName(), header.getValue());
            }

            /* Add the page response code to headers */
            headersMap.put(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE, response.getStatusLine().getStatusCode() + "");
        } catch (Exception exc) {
            LOG.error("Cannot process page: " + exc.getMessage(), exc);

            headersMap.put(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE, WebCrawlerConstants.HTTP_RESPONSE_CODE_NONE);
        }

        return headersMap;
    }

    /**
     * Are the headers mime types accepted
     *
     * @param contentTypeHeader The page "content type" header
     * @return {@link java.lang.Boolean} {@code true} or {@code false}
     */
    public static synchronized boolean isMediaTypeAccepted(final String contentTypeHeader) {
        final String[] mimeParts = contentTypeHeader.trim().split(";");
        for (final String mimePart : mimeParts) {
            if (Arrays.binarySearch(WebCrawlerConstants.ACCEPTED_MIME_TYPES, mimePart.trim()) >= 0) {
                return true;
            }
        }

        LOG.debug("Mime type " + contentTypeHeader + " not accepted");

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
