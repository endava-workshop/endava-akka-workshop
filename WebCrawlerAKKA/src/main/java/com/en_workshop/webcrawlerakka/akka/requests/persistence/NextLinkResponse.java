package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.Link;
import org.apache.log4j.Logger;

/**
 * Send the next link to be crawled.
 *
 * @author Radu Ciumag
 */
public class NextLinkResponse extends MessageResponse {
    private static final Logger LOG = Logger.getLogger(NextLinkResponse.class);

    private final Link nextLink;

    public NextLinkResponse(final NextLinkRequest nextLinkRequest, final Link nextLink) {
        super(System.currentTimeMillis(), nextLinkRequest);

        this.nextLink = nextLink;
    }

    public NextLinkRequest getNextLinkRequest() {
        return (NextLinkRequest) getMessageRequest();
    }

    public Link getNextLink() {
        return nextLink;
    }
}
