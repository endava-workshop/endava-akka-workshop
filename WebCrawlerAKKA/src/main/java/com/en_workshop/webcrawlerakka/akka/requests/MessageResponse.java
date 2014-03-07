package com.en_workshop.webcrawlerakka.akka.requests;

import org.apache.log4j.Logger;

/**
 * @author Radu Ciumag
 */
public abstract class MessageResponse {
    private static final Logger LOG = Logger.getLogger(MessageResponse.class);

    private final long id;
    private final MessageRequest messageRequest;

    public MessageResponse(final long id, final MessageRequest messageRequest) {
        this.id = id;
        this.messageRequest = messageRequest;
    }

    public long getId() {
        return id;
    }

    public MessageRequest getMessageRequest() {
        return messageRequest;
    }
}
