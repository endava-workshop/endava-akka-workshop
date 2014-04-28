package com.en_workshop.webcrawlerakka.akka.requests.domain;

import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * User: Roxana Paduraru
 * Date: 4/28/14
 */
public class DomainStoppedMessage {

    private final Domain domain;

    public DomainStoppedMessage(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
