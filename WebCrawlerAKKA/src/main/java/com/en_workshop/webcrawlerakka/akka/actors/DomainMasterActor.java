package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.requests.CrawleDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.StartDomainMasterRequest;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;

/**
 * Domains master actor
 *
 * @author Radu Ciumag
 */
public class DomainMasterActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(DomainMasterActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof StartDomainMasterRequest) {
            LOG.info("StartDomainMasterRequest: " + message);

            /* Get list of all domains and start actors for each */
            for (WebDomain webDomain : WebDomain.DOMAINS) {
                ActorRef domainActor = getContext().actorOf(Props.create(DomainActor.class), WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + webDomain.getName());
                domainActor.tell(new CrawleDomainRequest(webDomain), getSelf());
            }

            LOG.info("StartDomainMasterRequest: DONE");
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}