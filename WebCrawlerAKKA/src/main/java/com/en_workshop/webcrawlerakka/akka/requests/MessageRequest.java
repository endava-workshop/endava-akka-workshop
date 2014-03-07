package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public abstract class MessageRequest {
    private static final Logger LOG = Logger.getLogger(MessageRequest.class);

    private final long id;

    public MessageRequest(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
