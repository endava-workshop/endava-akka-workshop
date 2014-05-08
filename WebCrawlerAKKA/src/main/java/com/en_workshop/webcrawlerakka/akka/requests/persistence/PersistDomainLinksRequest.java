package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.DomainLink;

import java.util.List;

/**
 * Request to persist a list of domain links.
 *
 * Created by roxana on 3/13/14.
 */
public class PersistDomainLinksRequest extends MessageRequest implements PersistenceRequest{

    private final List<DomainLink> domainLinks;

    public PersistDomainLinksRequest(List<DomainLink> domainLinks) {
        super(System.currentTimeMillis());
        this.domainLinks = domainLinks;
    }

    public List<DomainLink> getDomainLinks() {
        return domainLinks;
    }
}
