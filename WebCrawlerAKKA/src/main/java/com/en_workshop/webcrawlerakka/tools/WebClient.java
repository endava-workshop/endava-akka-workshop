package com.en_workshop.webcrawlerakka.tools;

import com.codahale.metrics.Timer;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.metrics.Metrics;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Radu Ciumag
 */
public class WebClient {

    private static Timer pageHeadersTimer = Metrics.getInstance().timer("page-headers");
    private static Timer pageContentTimer = Metrics.getInstance().timer("page-content");

    private static final Logger LOG = LoggerFactory.getLogger(WebClient.class);

    public static final String PROTOCOL_HTTP = "http";
    public static final int PROTOCOL_HTTP_PORT = 80;
    public static final String PROTOCOL_HTTPS = "https";
    public static final int PROTOCOL_HTTPS_PORT = 443;

    private static CloseableHttpClient httpClient;
    static {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setMaxRedirects(2)
                .setCircularRedirectsAllowed(false)
                .setSocketTimeout(30000)
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .build();
        httpClient = HttpClients.createDefault();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .setConnectionManager(cm)
                .build();

    }


    //TODO Set and use User-Agent

    /**
     * Get the page headers for a specified link
     *
     * @param link The link to access
     * @return The list of {@link org.apache.http.Header}s
     */
    public static Map<String, String> getPageHeaders(final String link) throws IOException {
        long t0 = System.currentTimeMillis();
        Timer.Context time = pageHeadersTimer.time();
        try {
            final Map<String, String> headersMap = new HashMap<>();

            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpHead headRequest = new HttpHead(link);
            final CloseableHttpResponse response = httpClient.execute(headRequest);
            /* Add page headers */
            final Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                headersMap.put(header.getName(), header.getValue());
            }

            /* Add the page response code to headers */
            headersMap.put(WebCrawlerConstants.HTTP_CUSTOM_HEADER_RESPONSE_CODE, response.getStatusLine().getStatusCode() + "");

            return headersMap;
        } finally {
            long ns = time.stop();
            long t1 = System.currentTimeMillis();
            long l = (t1 - t0) / 1000;
            if (l> 10) {
                System.out.println(l + "s - headers for " + link);
            }

//            System.out.println(TimeUnit.SECONDS.convert(ns, TimeUnit.NANOSECONDS)+ " - headers for " +link);
        }
    }

    /**
     * Are the headers mime types accepted
     *
     * @param contentTypeHeader The page "content type" header
     * @return {@link java.lang.Boolean} {@code true} or {@code false}
     */
    public static boolean isMediaTypeAccepted(final String contentTypeHeader) {
        if (StringUtils.isBlank(contentTypeHeader)) {
            return false;
        }
        final String[] mimeParts = contentTypeHeader.trim().split(";");
        for (final String mimePart : mimeParts) {
            if (Arrays.binarySearch(WebCrawlerConstants.ACCEPTED_MIME_TYPES, mimePart.trim()) >= 0) {
                return true;
            }
        }

        LOG.debug("Mime type " + contentTypeHeader + " not accepted");

        return false;
    }

    public static boolean isProtocolAccepted(final String link) throws MalformedURLException {
        URL url = new URL(link);

        if (Arrays.binarySearch(WebCrawlerConstants.ACCEPTED_PROTOCOLS, url.getProtocol()) >= 0) {
            return true;
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
    public static String getPageContent(final String link) throws IOException {
        long t0 = System.currentTimeMillis();
        Timer.Context time = pageContentTimer.time();
        try {
            String content = null;

            final HttpGet getRequest = new HttpGet(link);

            try (final CloseableHttpResponse response = httpClient.execute(getRequest)) {
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (final InputStream responseStream = entity.getContent()) {
                        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));
                        final StringBuilder contentBuilder = new StringBuilder();
                        String line;
                        while (null != (line = bufferedReader.readLine())) {
                            contentBuilder.append(line).append('\n');
                        }

                        content = contentBuilder.toString();
                    }
                }
            }

            return content;
        } finally {
            long ns = time.stop();
            long t1 = System.currentTimeMillis();
            long l = (t1 - t0) / 1000;
            if (l> 10) {
                System.out.println(l + "s - content for " + link);
            }
//            System.out.println(TimeUnit.SECONDS.convert(ns, TimeUnit.NANOSECONDS) + " - content for " + link);
        }
    }

    /**
     * Verify if an {@link java.net.URL} can be obtained from the specified link.
     *
     * @param link the link.
     * @return {@code true} if an {@link java.net.URL} can be obtained from the specified link, {@code false} otherwise.
     */
    public static boolean isValid(String link) {
        URL url = null;

        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            url.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }
}
