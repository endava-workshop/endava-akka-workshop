package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.WebUrl;
import org.apache.log4j.Logger;

/**
 * Send the next link to be crawled
 *
 * @author Radu Ciumag
 */
public class NextLinkResponse extends MessageResponse {
    private static final Logger LOG = Logger.getLogger(NextLinkResponse.class);

    private final WebUrl nextLink;

    public NextLinkResponse(final NextLinkRequest nextLinkRequest, final WebUrl nextLink) {
        super(System.currentTimeMillis(), nextLinkRequest);

        this.nextLink = nextLink;
    }

    public NextLinkRequest getNextLinkRequest() {
        return (NextLinkRequest) messageRequest;
    }

    public WebUrl getNextLink() {
        return nextLink;
    }
}
