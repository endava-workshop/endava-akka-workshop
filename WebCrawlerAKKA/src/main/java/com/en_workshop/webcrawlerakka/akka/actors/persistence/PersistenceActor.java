package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.*;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;

import java.util.List;

/**
 * Actor for managing the persistence requests.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class PersistenceActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ListDomainsRequest) {
            processListDomainsRequest((ListDomainsRequest) message);
        } else if (message instanceof NextLinkRequest) {
            processNextLinkRequest((NextLinkRequest) message);
        }  else if (message instanceof PersistLinkRequest) {
            processPersistLinkRequest((PersistLinkRequest) message);
        } else if (message instanceof PersistDomainRequest) {
            processPersistDomainRequest((PersistDomainRequest) message);
        } else if (message instanceof PersistContentRequest){
            processPersistContentRequest((PersistContentRequest) message);
        }else {
            LOG.error("Unknown message: " + message);
        }
    }


    private void processListDomainsRequest(ListDomainsRequest request) {
        // TODO Remove after the persistence infrastructure is up and running
        List<Domain> domains = DomainDao.findAll();

        LOG.debug("List of domains found: " + domains);

        ListDomainsResponse response = new ListDomainsResponse(request, domains);
        getSender().tell(response, getSelf());
    }

    private void processNextLinkRequest(NextLinkRequest request){
        // TODO Remove after the persistence infrastructure is up and running
        Link link = LinkDao.getNextForCrawling(request.getDomain());
        NextLinkResponse response = new NextLinkResponse(request, link);

        LOG.debug("Found next URL for crawling: " + (null == link ? "NONE" : link.getUrl()));

        getSender().tell(response, getSelf());
    }

    private void processPersistLinkRequest(PersistLinkRequest persistLinkRequest) {
        LOG.info("Received link to persist: " + persistLinkRequest.getLink().getUrl());

        // TODO Remove after the persistence infrastructure is up and running
        LinkDao.update(persistLinkRequest.getLink());
    }

    private void processPersistDomainRequest(PersistDomainRequest persistDomainRequest) {
        LOG.info("Received domain to persist: " + persistDomainRequest.getDomain().getName());

        // TODO Remove after the persistence infrastructure is up and running
        DomainDao.update(persistDomainRequest.getDomain());
    }

    private void processPersistContentRequest(PersistContentRequest persistContentRequest) {
        LOG.info("Received content to persist: " + persistContentRequest.getPage().getContent().length());
        //TODO call the persist content
    }
}
