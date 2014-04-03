package com.en_workshop.webcrawlerakka.akka.requests;

/**
 * @author Radu Ciumag
 */
public abstract class MessageResponse {

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
