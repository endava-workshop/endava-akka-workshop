package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.*;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.routing.FromConfig;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.CrawlDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.RefreshDomainMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkResponse;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistenceRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessingRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddDomainRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddLinkRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.StatisticsRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.exceptions.UnresponsiveDomainException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Domains master actor
 * TODO Remove DomainActors when they fail / have no more data to process.
 * TODO Stop actors that do not respond for 5 requests.
 * TODO The stopped domains shouldn't be removed from the domains map? They are occupying slots for nothing.
 *
 * @author Radu Ciumag
 */
public class DomainMasterActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private final Map<String, ActorRef> domainActors;
    //TODO restart the actors after the specified amount of time
    private final Map<String, Long> stoppedDomains;

    private final ActorRef downloadUrlsRouter;

    private final ActorRef parent; //MasterActor

    /**
     * The default constructor.
     */
    public DomainMasterActor(ActorRef parent) {
        LOG.error("\nCreating DomainMasterActor\n");
        this.domainActors = new HashMap<>();
        this.stoppedDomains = new HashMap<>();

        this.parent = parent;

        final SupervisorStrategy downloadUrlsRouterStrategy = new OneForOneStrategy(-1, Duration.create(1, TimeUnit.MINUTES),
                new Function<Throwable, SupervisorStrategy.Directive>() {
                    @Override
                    public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                        LOG.error("Exception in DomainMasterActor routersSupervisorStrategy: type [" + throwable.getClass() + "], message [" + throwable.getMessage() + "]. Will restart.");
                        return SupervisorStrategy.restart();

                    }
                }
        );

        this.downloadUrlsRouter = getContext().actorOf(Props.create(DownloadUrlActor.class).withRouter(new FromConfig().withSupervisorStrategy(downloadUrlsRouterStrategy)),
                "downloadUrlRouter");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof RefreshDomainMasterRequest) {
            /* Send a "find domains" request to the persistence master */
            getParent().tell(new ListDomainsRequest(), getSelf());
        } else if (message instanceof ListDomainsResponse) {
            final ListDomainsResponse response = (ListDomainsResponse) message;

            /* Check domains processing limit */
            if (domainActors.size() >= WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT) {
                LOG.info("The number of maximum actors for domains processing was reached. (MAX = " + WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT + ")");
            }

            int slotsLeft = WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT - domainActors.size();
                /* Start an actor for each domain, if not already started */
            for (final Domain domain : response.getDomains()) {
                    /* Is this domain stopped? */
                if (stoppedDomains.containsKey(domain.getName())) {
                    LOG.info("Stopped domain: " + domain.getName() + ". This domain will not be processed.");
                    continue;
                }

                /* Is this domain in processing? */
                if (!domainActors.containsKey(domain.getName()) && slotsLeft > 0) {
                    startNewDomain(domain);
                    slotsLeft--;
                }
            }

            /* Schedule a domains list refresh */
            getContext().system().scheduler().scheduleOnce(Duration.create(WebCrawlerConstants.DOMAINS_REFRESH_PERIOD, TimeUnit.MILLISECONDS),
                    getSelf(), new RefreshDomainMasterRequest(), getContext().system().dispatcher(), getSelf());
        } else  if (message instanceof NextLinkResponse) {
            NextLinkResponse nextLink = (NextLinkResponse) message;
            /* If there was no next link found for that domain, remove the domain actor */
            String domainName = nextLink.getNextLinkRequest().getDomain().getName();
            if (null == nextLink.getNextLink()) {
                ActorRef domainActor = domainActors.get(domainName);
                domainActors.remove(domainActor);
                /* Stop the actor? */
//                domainActor.tell(PoisonPill.getInstance(), getSelf());
            } else {
                if (domainActors.get(domainName) != null) {
                    domainActors.get(domainName).tell(nextLink, getSelf());
                }
            }
        } else  if (message instanceof StatisticsRequest) {
            getParent().tell(message, getSelf());
        } else  if (message instanceof ProcessingRequest) {
            getParent().tell(message, getSelf());
        } else  if (message instanceof PersistenceRequest) {
            getParent().tell(message, getSelf());
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
        final ActorRef domainActor = getContext().actorOf(Props.create(DomainActor.class, downloadUrlsRouter, getSelf()),
                WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + getActorName(domain.getName()));

        /* Add to the domain map and ensure one actor per domain */
        domainActors.put(domain.getName(), domainActor);

        LOG.info("Domain " + domain.getName() + " starting actor " + domainActor);

        domainActor.tell(new CrawlDomainRequest(domain), getSelf());

        getParent().tell(new AddDomainRequest(domain), getSelf());
    }


    private final SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(1, TimeUnit.MINUTES),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override
                public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    if (throwable instanceof UnresponsiveDomainException) {
                        UnresponsiveDomainException unresponsiveDomainException = (UnresponsiveDomainException) throwable;
                        String domainName = unresponsiveDomainException.getDomain().getName();
                        if (!stoppedDomains.containsKey(domainName)) {
                            stoppedDomains.put(domainName, Calendar.getInstance().getTimeInMillis());
                            domainActors.remove(domainName);
                        }
                        return SupervisorStrategy.stop();
                    }

                    return SupervisorStrategy.restart();
                }
            }
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }

    public ActorRef getParent() {
        return parent;
    }
}