package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * Request to save/update a link.
 *
 * Created by roxana on 3/12/14.
 */
public class UpdateLinkRequest extends MessageRequest implements PersistenceRequest{

    private final Link link;
    private final String source;

    public UpdateLinkRequest(Link link, String source) {
        super(System.currentTimeMillis());
        this.link = link;
        this.source = source;
    }

    public UpdateLinkRequest(Link link) {
        this(link, null);
    }

    public Link getLink() {
        return link;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "UpdateLinkRequest{" +
                "link=" + link +
                ", source='" + source + '\'' +
                '}';
    }
}
