package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.CrawlDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.RefreshDomainMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsResponse;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddDomainRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Domains master actor
 *
 * @author Radu Ciumag
 */
public class DomainMasterActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

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
            findLocalActor(WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME, new OnSuccess<ActorRef>() {
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
            for (final Domain domain : response.getDomains()) {
                if (!domainActors.containsKey(domain.getName())) {
                    startNewDomain(domain);
                }
            }

            /* Schedule a domains list refresh */
            getContext().system().scheduler().scheduleOnce(Duration.create(WebCrawlerConstants.DOMAINS_REFRESH_PERIOD, TimeUnit.MILLISECONDS),
                    getSelf(), new RefreshDomainMasterRequest(), getContext().system().dispatcher(), getSelf());
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }

    /**
     * Creates an actor for the new domain, sends a crawl request and calls the statistics actor.
     *
     * @param domain the new domain.
     */
    private void startNewDomain(final Domain domain) {
        final ActorRef domainActor = getContext().actorOf(Props.create(DomainActor.class),
                WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + domain.getName().replace('.','_').replace(':', '_').replace('/', '_'));

        /* Add to the domain map and ensure one actor per domain */
        domainActors.put(domain.getName(), domainActor);

        LOG.info("Domain " + domain.getName() + " starting actor " + domainActor);

        domainActor.tell(new CrawlDomainRequest(domain), getSelf());

        /* Report to the statistics actor. */
        findLocalActor(WebCrawlerConstants.STATISTICS_ACTOR_NAME, new OnSuccess<ActorRef>() {
                    @Override
                    public void onSuccess(ActorRef statisticsActor) throws Throwable {
                        statisticsActor.tell(new AddDomainRequest(domain), getSelf());
                    }
                }, new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        LOG.error("Cannot find Statistics Actor.");
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }
}