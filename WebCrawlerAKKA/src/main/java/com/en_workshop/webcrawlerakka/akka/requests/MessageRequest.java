package com.en_workshop.webcrawlerakka.akka.requests;

/**
 * @author Radu Ciumag
 */
public abstract class MessageRequest {

    private final long id;

    public MessageRequest(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
