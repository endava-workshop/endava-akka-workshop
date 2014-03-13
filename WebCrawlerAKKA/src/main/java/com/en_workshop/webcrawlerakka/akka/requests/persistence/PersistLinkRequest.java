package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.WebUrl;

/**
 * Request to persist a link.
 *
 * Created by roxana on 3/12/14.
 */
public class PersistLinkRequest extends MessageRequest {

    private final WebUrl link;

    public PersistLinkRequest(WebUrl link) {
        super(System.currentTimeMillis());
        this.link = link;
    }

    public WebUrl getLink() {
        return link;
    }
}
