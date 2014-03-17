package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.actor.UntypedActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsResponse;
import com.en_workshop.webcrawlerakka.dao.WebDomainDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Identify the list of domains to scan
 *
 * @author Radu Ciumag
 */
public class ListDomainsActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(ListDomainsActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ListDomainsRequest) {
            ListDomainsRequest request = (ListDomainsRequest) message;

            List<Domain> domains = WebDomainDao.findAll();

            LOG.debug("List of domains found: " + domains);

            ListDomainsResponse response = new ListDomainsResponse(request, domains);
            getSender().tell(response, getSelf());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}