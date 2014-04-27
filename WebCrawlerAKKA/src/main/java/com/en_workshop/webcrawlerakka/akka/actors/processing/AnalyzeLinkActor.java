package com.en_workshop.webcrawlerakka.akka.actors.processing;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistDomainLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.AnalyzeLinkRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.entities.DomainLink;
import com.en_workshop.webcrawlerakka.entities.Link;
import com.en_workshop.webcrawlerakka.tools.WebClient;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * Actor for analyzing the links. If the domain of the link is different than it's own, persist both link and domain.
 *
 * Created by roxana on 3/13/14.
 */
public class AnalyzeLinkActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private ActorRef parent;//ProcessingMasterActor

    public AnalyzeLinkActor(ActorRef parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof AnalyzeLinkRequest) {
            AnalyzeLinkRequest analyzeLinkRequest = (AnalyzeLinkRequest) message;

            Domain sourceDomain = analyzeLinkRequest.getSourceDomain();
            String sourceLink = analyzeLinkRequest.getSourceLink();
            String link = analyzeLinkRequest.getLink();

            //if the initial domain is not the same as the domain of the link, persist both domain and link
            if (StringUtils.isNotBlank(link) && WebClient.isValid(link) && WebClient.isProtocolAccepted(link))  {
                URL url = new URL(link);
                String linkDomain = url.getHost();

                LOG.info("Analyzing link: " + link + " and domain " + linkDomain);

                Domain newDomain = null;
                if (!linkDomain.equals(sourceDomain.getName())) {
                    newDomain = new Domain(url.getHost(), WebCrawlerConstants.DOMAIN_DEFAULT_COOLDOWN, 0);
                }

                persistDomainLink(null == newDomain ? sourceDomain : newDomain, new Link(linkDomain, sourceDomain.getName(), link, sourceLink));
            } else {
                LOG.debug("Invalid URL received [" + link + "]");
            }

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }

    /**
     * Finds the PersistenceMasterActor and sends the request to persist the link.
     *
     * @param domain the name of the web domain of the link.
     * @param link the link.
     */
    private void persistDomainLink(final Domain domain, final Link link) {
        //call to persist the normalized link
        getParent().tell(new PersistDomainLinkRequest(new DomainLink(domain, link)), getSelf());
    }

    public ActorRef getParent() {
        return parent;
    }
}
