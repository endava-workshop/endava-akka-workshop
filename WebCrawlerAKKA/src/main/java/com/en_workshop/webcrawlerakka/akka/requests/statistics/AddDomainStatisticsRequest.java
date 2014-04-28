package com.en_workshop.webcrawlerakka.akka.requests.statistics;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;

/**
 * Request to update the domain statistics.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class AddDomainStatisticsRequest extends MessageRequest implements StatisticsRequest {

    private final Domain domain;

    public AddDomainStatisticsRequest(Domain domain) {
        super(System.currentTimeMillis());
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }
}
