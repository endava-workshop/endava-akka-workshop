package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.Domain;

import java.util.Collections;
import java.util.List;

/**
 * Respond with the list of domains to crawl
 *
 * @author Roxana Paduraru
 */
public class ListCrawlableDomainsResponse extends MessageResponse {

    private final List<Domain> crawlableDomains;

    public ListCrawlableDomainsResponse(final ListCrawlableDomainsRequest listCrawlableDomainsRequest,
                                        final List<Domain> crawlableDomains) {
        super(System.currentTimeMillis(), listCrawlableDomainsRequest);

        this.crawlableDomains = Collections.unmodifiableList(crawlableDomains);
    }

    public List<Domain> getCrawlableDomains() {
        return crawlableDomains;
    }
}
