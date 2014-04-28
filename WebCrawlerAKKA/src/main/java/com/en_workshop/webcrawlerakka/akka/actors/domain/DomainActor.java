package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.*;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistenceRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessingRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.StatisticsRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.enums.DomainStatus;
import com.en_workshop.webcrawlerakka.exceptions.ExhaustedDomainException;
import com.en_workshop.webcrawlerakka.exceptions.UnresponsiveDomainException;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Domain controller actor
 *
 * @author Radu Ciumag
 */
public class DomainActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private int noOfConsecutiveFails;
    private int noOfConsecutiveEmptyNextLink;

    private final ActorRef parent; //DomainMasterActor
    private final Domain domain;

    /**
     * The constructor
     *
     * @param parent the actor that created the domain actor.
     */
    public DomainActor(final ActorRef parent, final Domain domain) {
        this.parent = parent;
        this.domain = domain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws UnresponsiveDomainException, ExhaustedDomainException {
        if (message instanceof CrawlDomainRequest) {
            final CrawlDomainRequest request = (CrawlDomainRequest) message;

            /* Send a "find next link for domain" request to the persistence master */
            getParent().tell(new NextLinkRequest(request.getDomain()), getSelf());

        } else if (message instanceof NextLinkResponse) {
            final NextLinkResponse response = (NextLinkResponse) message;
            final NextLinkRequest request = response.getNextLinkRequest();

            if (null == response.getNextLink()) {
                LOG.info("Domain " + request.getDomain().getName() + " has no more links to crawl");
                noOfConsecutiveEmptyNextLink++;
            } else {
                LOG.info("Domain " + response.getNextLinkRequest().getDomain().getName() + " crawling link: " + response.getNextLink().getUrl());
                noOfConsecutiveEmptyNextLink = 0;

                /* Send a "download URL" request */
                getParent().tell(new DownloadUrlRequest(request.getDomain(), response.getNextLink()), getSelf());
            }
            boolean limitReached = (noOfConsecutiveEmptyNextLink == WebCrawlerConstants.EMPTY_NEXT_LINK_TRIALS);
            if (limitReached) {
                throw new ExhaustedDomainException(request.getDomain());
            }
            /* Schedule a new crawl for the downloaded domain after the cool down period */
            getContext().system().scheduler().scheduleOnce(Duration.create(request.getDomain().getCoolDownPeriod(), TimeUnit.MILLISECONDS),
                    getSelf(), new CrawlDomainRequest(request.getDomain()), getContext().system().dispatcher(), getSelf());
        }else if (message instanceof DownloadUrlResponse) {
            final DownloadUrlResponse response = (DownloadUrlResponse) message;
            final Domain domain = response.getDownloadUrlRequest().getDomain();

            if (response.isUnresponsiveDomain()) {
                noOfConsecutiveFails++;
                boolean limitReached = (noOfConsecutiveFails == WebCrawlerConstants.CONNECTION_EXCEPTION_TRIALS);
                if (limitReached) {
                    throw new UnresponsiveDomainException(domain);
                }
            } else {
                noOfConsecutiveFails = 0;
            }
            /* Schedule a new crawl for the downloaded domain after the cool down period */
            getContext().system().scheduler().scheduleOnce(Duration.create(domain.getCoolDownPeriod(), TimeUnit.MILLISECONDS), getSelf(),
                    new CrawlDomainRequest(domain), getContext().system().dispatcher(), getSelf());
        } else if (message instanceof PersistenceRequest){
            getParent().tell(message, getSelf());
        } else if (message instanceof StatisticsRequest){
            getParent().tell(message, getSelf());
        } else if (message instanceof ProcessingRequest){
            getParent().tell(message, getSelf());
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    @Override
    public void postStop() throws Exception {
        /* Remove the actor from the map after it is stopped */
        getParent().tell(new DomainStoppedMessage(domain), getSelf());
        super.postStop();
    }

    @Override
    public void preStart() throws Exception {
        /* Add the actor to the map after it is started */
        Domain startedDomain = new Domain(domain.getName(), domain.getCoolDownPeriod(), domain.getCrawledAt(), DomainStatus.STARTED);
        getParent().tell(new DomainStartedMessage(startedDomain), getSelf());
        super.preStart();
    }

    public ActorRef getParent() {
        return parent;
    }

    public Domain getDomain() {
        return domain;
    }
}