package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.entities.Page;

/**
 * Request to persist the content of a link.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class PersistContentRequest extends MessageRequest implements PersistenceRequest{

   private final Page page;

    public PersistContentRequest(Link link, String content) {
        super(System.currentTimeMillis());
        page = new Page(link.getUrl(), content);
    }

    public Page getPage() {
        return page;
    }
}
