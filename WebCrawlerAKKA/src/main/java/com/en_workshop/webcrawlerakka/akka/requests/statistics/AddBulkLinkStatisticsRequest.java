package com.en_workshop.webcrawlerakka.akka.requests.statistics;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;

import java.util.List;

/**
 * Request to bulk update the link statistics.
 *
 * Created by roxana on 5/8/14.
 */
public class AddBulkLinkStatisticsRequest  extends MessageRequest implements StatisticsRequest {

    List<DomainLink> domainLinks;

    public AddBulkLinkStatisticsRequest(List<DomainLink> domainLinks) {
        super(System.currentTimeMillis());
        this.domainLinks = domainLinks;
    }

    public List<DomainLink> getDomainLinks() {
        return domainLinks;
    }
}
