package com.en_workshop.webcrawlerakka.exceptions;

import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * Exception thrown when the domain doesn't respond for a specified amount of times.
 */
public class UnresponsiveDomainException extends Exception {

    private Domain domain;

    public UnresponsiveDomainException(Domain domain) {
        super();
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
