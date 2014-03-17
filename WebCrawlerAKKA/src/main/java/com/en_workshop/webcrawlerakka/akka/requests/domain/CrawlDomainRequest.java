package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class CrawlDomainRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(CrawlDomainRequest.class);

    private final Domain domain;

    public CrawlDomainRequest(final Domain domain) {
        super(System.currentTimeMillis());

        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
