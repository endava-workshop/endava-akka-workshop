package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * User: Roxana Paduraru
 * Date: 4/28/14
 */
public class DomainStartedMessage {

    private final Domain domain;

    public DomainStartedMessage(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
