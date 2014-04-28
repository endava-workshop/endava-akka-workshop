package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.*;
import com.en_workshop.webcrawlerakka.dao.DomainDao;
import com.en_workshop.webcrawlerakka.dao.LinkDao;
import com.en_workshop.webcrawlerakka.dao.impl.InMemoryDomainDao;
import com.en_workshop.webcrawlerakka.dao.impl.InMemoryLinkDao;
import com.en_workshop.webcrawlerakka.dao.impl.RestDomainDao;
import com.en_workshop.webcrawlerakka.dao.impl.RestLinkDao;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.enums.DomainStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor for managing the persistence requests.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class PersistenceActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
    private static DomainDao domainDao = new InMemoryDomainDao();
//    private static DomainDao domainDao = new RestDomainDao();
    private static LinkDao linkDao = new InMemoryLinkDao();
//    private static LinkDao linkDao = new RestLinkDao();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ListDomainsRequest) {
            processListDomainsRequest((ListDomainsRequest) message);
        } else if(message instanceof ListCrawlableDomainsRequest) {
            processListCrawlabeDomainsRequest((ListCrawlableDomainsRequest) message);
        } else if (message instanceof NextLinkRequest) {
            processNextLinkRequest((NextLinkRequest) message);
        } else if (message instanceof UpdateLinkRequest) {
            processUpdateLinkRequest((UpdateLinkRequest) message);
        } else if (message instanceof UpdateDomainRequest) {
            processUpdateDomainRequest((UpdateDomainRequest) message);
        } else if (message instanceof PersistDomainLinkRequest) {
            processPersistDomainLinkRequest((PersistDomainLinkRequest) message);
        } else if (message instanceof PersistContentRequest){
            processPersistContentRequest((PersistContentRequest) message);
        }else {
            LOG.error("Unknown message: " + message);
        }
    }


    private void processListDomainsRequest(ListDomainsRequest request) {
        List<Domain> domains = domainDao.findAll(); // TODO consider async

        LOG.debug("List of domains found: " + domains);

        ListDomainsResponse response = new ListDomainsResponse(request, domains);
        getSender().tell(response, getSelf());
    }

    private void processListCrawlabeDomainsRequest(ListCrawlableDomainsRequest request) {
        List<DomainStatus> crawlableStatuses = new ArrayList<>();
        crawlableStatuses.add(DomainStatus.STARTED);
        crawlableStatuses.add(DomainStatus.FOUND);
        List<Domain> domains = domainDao.findAll(crawlableStatuses); // TODO consider async

        LOG.debug("List of crawlable domains found: " + domains);

        ListCrawlableDomainsResponse response = new ListCrawlableDomainsResponse(request, domains);
        getSender().tell(response, getSelf());
    }

    private void processNextLinkRequest(NextLinkRequest request){
        Link link = linkDao.getNextForCrawling(request.getDomain()); // TODO consider async
        NextLinkResponse response = new NextLinkResponse(request, link);

        LOG.debug("Found next URL for crawling: " + (null == link ? "NONE" : link.getUrl()));

        getSender().tell(response, getSelf());
    }

    private void processUpdateLinkRequest(UpdateLinkRequest updateLinkRequest) {
        LOG.info("Received link to update: " + updateLinkRequest.getLink().getUrl());
        linkDao.update(updateLinkRequest.getLink());
    }

    private void processUpdateDomainRequest(UpdateDomainRequest updateDomainRequest) {
        LOG.info("Received domain to update: " + updateDomainRequest.getDomain().getName());
        domainDao.update(updateDomainRequest.getDomain());
    }

    private void processPersistDomainLinkRequest(PersistDomainLinkRequest persistDomainRequest) {
        LOG.info("Received domain and link to persist: " + persistDomainRequest.getDomainLink());
        linkDao.create(persistDomainRequest.getDomainLink());
    }

    private void processPersistContentRequest(PersistContentRequest persistContentRequest) {
        LOG.info("Received content to persist: " + persistContentRequest.getPage().getContent().length());
        //TODO call the persist content
    }

    public static DomainDao getDomainDao() {
        return domainDao;
    }

    public static LinkDao getLinkDao() {
        return linkDao;
    }
}
