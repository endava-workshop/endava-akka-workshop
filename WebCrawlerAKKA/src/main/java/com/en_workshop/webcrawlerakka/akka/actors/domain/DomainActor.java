package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.CrawlDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistenceRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.UpdateLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessingRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.StatisticsRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
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

    private ActorRef parent; //DomainMasterActor
    private final ActorRef downloadUrlsRouter;

    /**
     * The constructor
     *
     * @param downloadUrlsRouter
     */
    public DomainActor(final ActorRef downloadUrlsRouter, final ActorRef parent) {
        this.downloadUrlsRouter = downloadUrlsRouter;
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws UnresponsiveDomainException {
        if (message instanceof CrawlDomainRequest) {
            final CrawlDomainRequest request = (CrawlDomainRequest) message;

            /* Send a "find next link for domain" request to the persistence master */
            getParent().tell(new NextLinkRequest(request.getDomain()), getSelf());

        } else if (message instanceof NextLinkResponse) {
            final NextLinkResponse response = (NextLinkResponse) message;
            final NextLinkRequest request = response.getNextLinkRequest();

            if (null == response.getNextLink()) {
                /* There is no next link */
                LOG.info("Domain " + request.getDomain().getName() + " has no more links to crawl");

                /* Schedule a new crawl for the downloaded domain after the cool down period */
                getContext().system().scheduler().scheduleOnce(Duration.create(request.getDomain().getCoolDownPeriod(), TimeUnit.MILLISECONDS),
                        getSelf(), new CrawlDomainRequest(request.getDomain()), getContext().system().dispatcher(), getSelf());

                return;
            }

            LOG.info("Domain " + response.getNextLinkRequest().getDomain().getName() + " crawling link: " + response.getNextLink().getUrl());

            /* Send a "download URL" request */
            downloadUrlsRouter.tell(new DownloadUrlRequest(request.getDomain(), response.getNextLink()), getSelf());

            // Here you can move the request for a new crawl on this domain. This will generate download link requests at a rate imposed only by the cool down period.
        }else if (message instanceof DownloadUrlResponse) {
            final DownloadUrlResponse response = (DownloadUrlResponse) message;
            final Domain domain = response.getDownloadUrlRequest().getDomain();

            if (response.isUnresponsiveDomain()) {
                noOfConsecutiveFails++;
                boolean limitReached = noOfConsecutiveFails == WebCrawlerConstants.CONNECTION_EXCEPTION_TRIALS;
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

    public ActorRef getParent() {
        return parent;
    }
}