package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 *
 * Created by roxana on 3/13/14.
 */
public class PersistDomainRequest extends MessageRequest {

    private final Domain domain;

    public PersistDomainRequest(Domain domain) {
        super(System.currentTimeMillis());
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
