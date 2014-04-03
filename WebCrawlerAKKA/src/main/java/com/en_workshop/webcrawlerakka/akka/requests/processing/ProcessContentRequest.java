package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * @author Radu Ciumag
 */
public class ProcessContentRequest extends MessageRequest {

    private final Link source;
    private final String content;

    public ProcessContentRequest(final Link source, final String content) {
        super(System.currentTimeMillis());

        this.source = source;
        this.content = content;
    }

    public Link getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }
}
