package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.requests.*;
import org.apache.log4j.Logger;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Domain controller actor
 *
 * @author Radu Ciumag
 */
public class DomainActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(DomainActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof CrawleDomainRequest) {
            LOG.info("CrawleDomainRequest: " + message);

            final CrawleDomainRequest crawleRequest = (CrawleDomainRequest) message;

            /* Find next url to download. Try to reuse the actor responsible for getting the next url, if it is created. */
            Future<ActorRef> nextUrlSearch = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
                    WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME + "/" + WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + crawleRequest.getWebDomain().getName() + "/" +
                    WebCrawlerConstants.NEXT_URL_ACTOR_PART_NAME + crawleRequest.getWebDomain().getName()).resolveOne(Duration.create(1, TimeUnit.SECONDS));
            nextUrlSearch.onSuccess(new OnSuccess<ActorRef>() {
                @Override
                public void onSuccess(ActorRef nextUrlActor) throws Throwable {
                    nextUrlActor.tell(new NextLinkRequest(crawleRequest.getWebDomain()), getSelf());
                }
            }, getContext().dispatcher());
            nextUrlSearch.onFailure(new OnFailure() {
                @Override
                public void onFailure(Throwable throwable) throws Throwable {
                    ActorRef nextUrlActor = getContext().actorOf(Props.create(NextLinkActor.class), WebCrawlerConstants.NEXT_URL_ACTOR_PART_NAME + crawleRequest.getWebDomain().getName());
                    nextUrlActor.tell(new NextLinkRequest(crawleRequest.getWebDomain()), getSelf());
                }
            }, getContext().dispatcher());

            LOG.info("CrawleDomainRequest: DONE");
        } else if (message instanceof NextLinkResponse) {
            LOG.info("NextLinkResponse: " + message);

            final NextLinkResponse nextLinkResponse = (NextLinkResponse) message;

            if (null == nextLinkResponse.getNextLink()) {
                /* There is no next link */
                return;
            }

            /* Download the provided url. Try to reuse the actor responsible for downloading the url, if it is created. */
            Future<ActorRef> downloadUrlSearch = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
                    WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME + "/" + WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + nextLinkResponse.getNextLinkRequest().getWebDomain().getName() +
                    "/" + WebCrawlerConstants.DOWNLOAD_URL_ACTOR_PART_NAME + nextLinkResponse.getNextLinkRequest().getWebDomain().getName()).
                    resolveOne(Duration.create(1, TimeUnit.SECONDS));
            downloadUrlSearch.onSuccess(new OnSuccess<ActorRef>() {
                @Override
                public void onSuccess(ActorRef downloadUrlActor) throws Throwable {
                    downloadUrlActor.tell(new DownloadUrlRequest(nextLinkResponse.getNextLink()), getSelf());
                }
            }, getContext().dispatcher());
            downloadUrlSearch.onFailure(new OnFailure() {
                @Override
                public void onFailure(Throwable throwable) throws Throwable {
                    ActorRef downloadUrlActor = getContext().actorOf(Props.create(DownloadUrlActor.class), WebCrawlerConstants.DOWNLOAD_URL_ACTOR_PART_NAME +
                            nextLinkResponse.getNextLinkRequest().getWebDomain().getName());
                    downloadUrlActor.tell(new DownloadUrlRequest(nextLinkResponse.getNextLink()), getSelf());
                }
            }, getContext().dispatcher());

            LOG.info("NextLinkResponse: DONE");
        } else if (message instanceof DownloadUrlResponse) {
            LOG.info("DownloadUrlResponse: " + message);

            DownloadUrlResponse downloadResponse = (DownloadUrlResponse) message;

            /* Schedule a new crawle for the downloaded domain after the cooldown period */
            getContext().system().scheduler().scheduleOnce(Duration.create(downloadResponse.getDownloadUrlRequest().getWebUrl().getWebDomain().getCooldownPeriod(), TimeUnit.MILLISECONDS),
                    getSelf(), new CrawleDomainRequest(downloadResponse.getDownloadUrlRequest().getWebUrl().getWebDomain()), getContext().system().dispatcher(), getSelf());

            LOG.info("DownloadUrlResponse: DONE");
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}