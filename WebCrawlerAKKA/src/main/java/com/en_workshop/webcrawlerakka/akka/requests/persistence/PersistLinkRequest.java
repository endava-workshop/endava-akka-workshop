package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * Request to persist a link.
 *
 * Created by roxana on 3/12/14.
 */
public class PersistLinkRequest extends MessageRequest implements PersistenceRequest{

    private final Link link;

    public PersistLinkRequest(Link link) {
        super(System.currentTimeMillis());
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
