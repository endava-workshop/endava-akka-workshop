package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * @author Radu Ciumag
 */
public class CrawlDomainRequest extends MessageRequest {

    private final Domain domain;

    public CrawlDomainRequest(final Domain domain) {
        super(System.currentTimeMillis());

        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
