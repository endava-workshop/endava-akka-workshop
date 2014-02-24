package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.CrawlDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.RefreshDomainMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsResponse;
import com.en_workshop.webcrawlerakka.entities.WebDomain;
import org.apache.log4j.Logger;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Domains master actor
 *
 * @author Radu Ciumag
 */
public class DomainMasterActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(DomainMasterActor.class);

    private final HashMap<String, ActorRef> domainActors;

    /**
     * The default constructor.
     */
    public DomainMasterActor() {
        this.domainActors = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof RefreshDomainMasterRequest) {
            /* Send a "find domains" request to the persistence master */
            findActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
                        @Override
                        public void onSuccess(ActorRef persistenceMasterActor) throws Throwable {
                            persistenceMasterActor.tell(new ListDomainsRequest(), getSelf());
                        }
                    }, new OnFailure() {
                        @Override
                        public void onFailure(Throwable throwable) throws Throwable {
                            LOG.error("Cannot find Persistence Master.");
                        }
                    }
            );
        } else if (message instanceof ListDomainsResponse) {
            final ListDomainsResponse response = (ListDomainsResponse) message;

            /* Start an actor for each domain, if not already started */
            for (final WebDomain webDomain : response.getWebDomains()) {
                if (!domainActors.containsKey(webDomain.getName())) {
                    final ActorRef domainActor = getContext().actorOf(Props.create(DomainActor.class), WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + webDomain.getName());
                    domainActors.put(webDomain.getName(), domainActor);

                    LOG.info("Domain " + webDomain.getName() + " starting actor " + domainActor);

                    domainActor.tell(new CrawlDomainRequest(webDomain), getSelf());
                }
            }

            /* Schedule a domains list refresh */
            getContext().system().scheduler().scheduleOnce(Duration.create(WebCrawlerConstants.DOMAINS_REFRESH_PERIOD, TimeUnit.MILLISECONDS),
                    getSelf(), new RefreshDomainMasterRequest(), getContext().system().dispatcher(), getSelf());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}