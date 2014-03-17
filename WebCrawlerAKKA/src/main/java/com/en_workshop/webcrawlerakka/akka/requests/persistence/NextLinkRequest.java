package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;

/**
 * Request a new link to crawl.
 *
 * @author Radu Ciumag
 */
public class NextLinkRequest extends MessageRequest {

    private static final Logger LOG = Logger.getLogger(NextLinkRequest.class);

    private final Domain domain;

    public NextLinkRequest(final Domain domain) {
        super(System.currentTimeMillis());

        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
