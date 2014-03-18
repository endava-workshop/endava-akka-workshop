package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.AnalyzeLinkRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.Link;

import java.net.URL;

/**
 * Actor for analyzing the links. If the domain of the link is different than it's own, persist both link and domain.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkActor extends BaseActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof AnalyzeLinkRequest) {
            LOG.debug("Analyzing link - START");

            AnalyzeLinkRequest analyzeLinkRequest = (AnalyzeLinkRequest) message;

            String sourceUrl = analyzeLinkRequest.getSourceDomainName();
            String link = analyzeLinkRequest.getLink();

            LOG.info("Received link: " + link + " and domain " + sourceUrl);

            //if the initial domain is not the same as the domain of the link, persist both domain and link
            URL url = new URL(link);
            String linkDomain = url.getHost();

            LOG.info("Analyzing link: " + link + " and domain " + linkDomain);

            Domain newDomain = null;
            if (!linkDomain.equals(sourceUrl)) {
                newDomain = new Domain(url.getHost(), 20000, 0);
                persistDomain(newDomain);
            }

            persistLink(newDomain == null ? analyzeLinkRequest.getSourceDomainName() : newDomain.getName(), link);

            LOG.debug("Analyzing link - STOP");

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }

    /**
     * Finds the PersistenceMasterActor and sends the request to persist the link.
     *
     * @param domainName the name of the web domain of the link.
     * @param link the link.
     */
    private void persistLink(final String domainName, final String link) {
        //call to persist the normalized link
        findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                    @Override
                    public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                        persistenceMasterActor.tell(new PersistLinkRequest(new Link(domainName, link)), getSelf());
                    }
                }, new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        LOG.error("Cannot find Persistence Master");
                    }
                }
        );
    }

    /**
     * Finds the PersistenceMasterActor and sends the request to persist the web domain.
     *
     * @param domain the web domain of the link.
     */
    private void persistDomain(final Domain domain) {
        //call to persist the normalized link
        findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                    @Override
                    public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                        persistenceMasterActor.tell(new PersistDomainRequest(domain), getSelf());
                    }
                }, new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        LOG.error("Cannot find Persistence Master");
                    }
                }
        );
    }
}
