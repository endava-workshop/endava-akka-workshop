package com.en_workshop.webcrawlerakka.exceptions;

import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * Exception thrown when there are no more links to download for the domain.
 *
 * User: rpaduraru
 * Date: 4/27/14
 */
public class ExhaustedDomainException extends Exception {

    private Domain domain;

    public ExhaustedDomainException(Domain domain) {
        super();
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }

}
