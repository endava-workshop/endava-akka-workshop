package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;

/**
 *
 * Created by roxana on 3/13/14.
 */
public class PersistDomainLinkRequest extends MessageRequest implements PersistenceRequest{

    private final DomainLink domainLink;

    public PersistDomainLinkRequest(DomainLink domainLink) {
        super(System.currentTimeMillis());
        this.domainLink = domainLink;
    }

    public DomainLink getDomainLink() {
        return domainLink;
    }
}
