package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.japi.Function;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.CrawlDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
import com.en_workshop.webcrawlerakka.entities.Domain;
import org.apache.log4j.Logger;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Domain controller actor
 *
 * @author Radu Ciumag
 */
public class DomainActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(DomainActor.class);

    private final SupervisorStrategy supervisorStrategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override
                public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    if (throwable instanceof Exception) {
                        return SupervisorStrategy.restart();
                    }

                    return SupervisorStrategy.stop();
                }
            });

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof CrawlDomainRequest) {
            final CrawlDomainRequest request = (CrawlDomainRequest) message;

            /* Send a "find next link for domain" request to the persistence master */
            findLocalActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                        @Override
                        public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                            persistenceMasterActor.tell(new NextLinkRequest(request.getDomain()), getSelf());
                        }
                    }, new OnFailure() {
                        @Override
                        public void onFailure(Throwable throwable) throws Throwable {
                            LOG.error("Cannot find Persistence Master.");
                        }
                    }
            );

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
            findLocalActor(WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME + "/" + WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME +
                    getActorName(response.getNextLinkRequest().getDomain().getName()) + "/" + WebCrawlerConstants.DOWNLOAD_URL_ACTOR_NAME, new OnSuccess<ActorRef>() {
                        @Override
                        public void onSuccess(ActorRef downloadUrlActor) throws Throwable {
                            downloadUrlActor.tell(new DownloadUrlRequest(request.getDomain(), response.getNextLink()), getSelf());
                        }
                    }, new OnFailure() {
                        @Override
                        public void onFailure(Throwable throwable) throws Throwable {
                            ActorRef downloadUrlActor = getContext().actorOf(Props.create(DownloadUrlActor.class), WebCrawlerConstants.DOWNLOAD_URL_ACTOR_NAME);
                            downloadUrlActor.tell(new DownloadUrlRequest(request.getDomain(), response.getNextLink()), getSelf());
                        }
                    }
            );

        } else if (message instanceof DownloadUrlResponse) {
            DownloadUrlResponse response = (DownloadUrlResponse) message;
            final Domain domain = response.getDownloadUrlRequest().getDomain();

            /* Schedule a new crawl for the downloaded domain after the cool down period */
            getContext().system().scheduler().scheduleOnce(Duration.create(domain.getCoolDownPeriod(), TimeUnit.MILLISECONDS),
                    getSelf(), new CrawlDomainRequest(domain), getContext().system().dispatcher(), getSelf());
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }
}