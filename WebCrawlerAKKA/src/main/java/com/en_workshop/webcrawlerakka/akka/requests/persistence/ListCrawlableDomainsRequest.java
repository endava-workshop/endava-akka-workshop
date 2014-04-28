package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;

/**
 * Request the list of domains to crawl
 *
 * @author Roxana Paduraru
 */
public class ListCrawlableDomainsRequest extends MessageRequest implements PersistenceRequest{

    /**
     * The default constructor
     */
    public ListCrawlableDomainsRequest() {
        super(System.currentTimeMillis());
    }
}
