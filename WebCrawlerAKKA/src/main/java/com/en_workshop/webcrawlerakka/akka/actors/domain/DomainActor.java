package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.CrawlDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof CrawlDomainRequest) {
            final CrawlDomainRequest request = (CrawlDomainRequest) message;

            /* Send a "find next link for domain" request to the persistence master */
            findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                        @Override
                        public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                            persistenceMasterActor.tell(new NextLinkRequest(request.getWebDomain()), getSelf());
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

            if (null == response.getNextLink()) {
                /* There is no next link */
                LOG.info("Domain " + response.getNextLinkRequest().getWebDomain().getName() + " has no more links to crawl");

                return;
            }

            LOG.info("Domain " + response.getNextLinkRequest().getWebDomain().getName() + " crawling link: " + response.getNextLink().getUrl());

            /* Send a "download URL" request */
            findActor(WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME + "/" + WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + response.getNextLinkRequest().getWebDomain().getName() +
                    "/" + WebCrawlerConstants.DOWNLOAD_URL_ACTOR_PART_NAME + response.getNextLinkRequest().getWebDomain().getName(), new OnSuccess<ActorRef>() {
                        @Override
                        public void onSuccess(ActorRef downloadUrlActor) throws Throwable {
                            downloadUrlActor.tell(new DownloadUrlRequest(response.getNextLink()), getSelf());
                        }
                    }, new OnFailure() {
                        @Override
                        public void onFailure(Throwable throwable) throws Throwable {
                            ActorRef downloadUrlActor = getContext().actorOf(Props.create(DownloadUrlActor.class), WebCrawlerConstants.DOWNLOAD_URL_ACTOR_PART_NAME +
                                    response.getNextLinkRequest().getWebDomain().getName());
                            downloadUrlActor.tell(new DownloadUrlRequest(response.getNextLink()), getSelf());
                        }
                    }
            );

        } else if (message instanceof DownloadUrlResponse) {
            DownloadUrlResponse response = (DownloadUrlResponse) message;

            /* Schedule a new crawl for the downloaded domain after the cool down period */
            getContext().system().scheduler().scheduleOnce(Duration.create(response.getDownloadUrlRequest().getWebUrl().getWebDomain().getCooldownPeriod(), TimeUnit.MILLISECONDS),
                    getSelf(), new CrawlDomainRequest(response.getDownloadUrlRequest().getWebUrl().getWebDomain()), getContext().system().dispatcher(), getSelf());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}