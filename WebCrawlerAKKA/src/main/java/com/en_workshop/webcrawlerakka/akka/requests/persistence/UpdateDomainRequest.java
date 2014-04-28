package com.en_workshop.webcrawlerakka.akka.requests.persistence;

import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * User: Roxana Paduraru
 * Date: 4/28/14
 */
public class UpdateDomainRequest implements PersistenceRequest{

    private final Domain domain;

    public UpdateDomainRequest(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
