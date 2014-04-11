package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * Request to persist the content of a link.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class PersistContentRequest extends MessageRequest implements PersistenceRequest{

    private Link link;
    private final String content;


    public PersistContentRequest(Link link, String content) {
        super(System.currentTimeMillis());
        this.link = link;
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
