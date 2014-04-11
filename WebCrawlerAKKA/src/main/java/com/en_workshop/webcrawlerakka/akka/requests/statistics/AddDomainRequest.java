package com.en_workshop.webcrawlerakka.akka.requests.statistics;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * Add comment.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class AddDomainRequest extends MessageRequest {

    private final Domain domain;

    public AddDomainRequest(Domain domain) {
        super(System.currentTimeMillis());
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
