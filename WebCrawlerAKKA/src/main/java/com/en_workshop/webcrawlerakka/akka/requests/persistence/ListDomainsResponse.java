package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Respond with the list of domains to crawl
 *
 * @author Radu Ciumag
 */
public class ListDomainsResponse extends MessageResponse {
    private static final Logger LOG = Logger.getLogger(ListDomainsResponse.class);

    private final List<WebDomain> webDomains;

    public ListDomainsResponse(final ListDomainsRequest listDomainsRequest, final List<WebDomain> webDomains) {
        super(System.currentTimeMillis(), listDomainsRequest);

        this.webDomains = webDomains;
    }

    public List<WebDomain> getWebDomains() {
        return webDomains;
    }
}
