package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Respond with the list of domains to crawl
 *
 * @author Radu Ciumag
 */
public class ListDomainsResponse extends MessageResponse {
    private static final Logger LOG = Logger.getLogger(ListDomainsResponse.class);

    private final List<Domain> domains;

    public ListDomainsResponse(final ListDomainsRequest listDomainsRequest, final List<Domain> domains) {
        super(System.currentTimeMillis(), listDomainsRequest);

        this.domains = domains;
    }

    public List<Domain> getDomains() {
        return domains;
    }
}
