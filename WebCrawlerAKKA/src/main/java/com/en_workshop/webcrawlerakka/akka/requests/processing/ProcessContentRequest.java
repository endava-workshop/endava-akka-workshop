package com.en_workshop.webcrawlerakka.akka.requests.processing;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;

/**
 * @author Radu Ciumag
 */
public class ProcessContentRequest extends MessageRequest implements ProcessingRequest {

    private final Domain sourceDomain;
    private final Link source;
    private final String content;

    public ProcessContentRequest(final Domain sourceDomain, final Link source, final String content) {
        super(System.currentTimeMillis());

        this.sourceDomain = sourceDomain;
        this.source = source;
        this.content = content;
    }

    public Domain getSourceDomain() {
        return sourceDomain;
    }

    public Link getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }
}
