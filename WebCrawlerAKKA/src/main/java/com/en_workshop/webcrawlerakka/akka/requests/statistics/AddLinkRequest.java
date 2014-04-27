package com.en_workshop.webcrawlerakka.akka.requests.statistics;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * Add comment.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class AddLinkRequest extends MessageRequest implements StatisticsRequest {

    private final String domain;
    private final Link link;


    public AddLinkRequest(String domain, Link link) {
        super(System.currentTimeMillis());
        this.domain = domain;
        this.link = link;
    }

    public String getDomain() {
        return domain;
    }

    public Link getLink() {
        return link;
    }
}
