package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;

/**
 * Request a new link to crawle
 *
 * @author Radu Ciumag
 */
public class NextLinkRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(NextLinkRequest.class);

    private final WebDomain webDomain;

    public NextLinkRequest(final WebDomain webDomain) {
        super(System.currentTimeMillis());

        this.webDomain = webDomain;
    }

    public WebDomain getWebDomain() {
        return webDomain;
    }
}
