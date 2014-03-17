package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public class DownloadUrlRequest extends MessageRequest {
    private static final Logger LOG = Logger.getLogger(DownloadUrlRequest.class);

    private final Domain domain;
    private final Link link;

    public DownloadUrlRequest(final Domain domain, final Link link) {
        super(System.currentTimeMillis());

        this.domain = domain;
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public Domain getDomain() {
        return domain;
    }
}
