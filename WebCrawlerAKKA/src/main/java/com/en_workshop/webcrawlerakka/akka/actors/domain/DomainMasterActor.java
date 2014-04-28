package com.en_workshop.webcrawlerakka.akka.actors.domain;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.domain.*;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.*;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessingRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.AddDomainStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.StatisticsRequest;
import com.en_workshop.webcrawlerakka.entities.Domain;
import com.en_workshop.webcrawlerakka.enums.DomainStatus;
import com.en_workshop.webcrawlerakka.exceptions.ExhaustedDomainException;
import com.en_workshop.webcrawlerakka.exceptions.UnresponsiveDomainException;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Domains master actor
 *
 * @author Radu Ciumag
 */
public class DomainMasterActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private final Map<String, ActorRef> domainActors;
    //TODO restart the actors after the specified amount of time
    private final Map<String, Long> unresponsiveDomains;
    private final Map<String, Long> exhaustedDomains;



    private final ActorRef parent; //MasterActor

    /**
     * The default constructor.
     */
    public DomainMasterActor(ActorRef parent) {
        LOG.error("\nCreating DomainMasterActor\n");
        this.domainActors = new HashMap<>();
        this.unresponsiveDomains = new HashMap<>();
        this.exhaustedDomains = new HashMap<>();

        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof RefreshDomainMasterRequest) {
            /* Send a "find domains" request to the persistence master */
            getParent().tell(new ListCrawlableDomainsRequest(), getSelf());
        } else if (message instanceof ListCrawlableDomainsResponse) {
            final ListCrawlableDomainsResponse response = (ListCrawlableDomainsResponse) message;

            /* Check domains processing limit */
            if (domainActors.size() >= WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT) {
                LOG.info("The number of maximum actors for domains processing was reached. (MAX = " + WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT + ")");
            }

            int slotsLeft = WebCrawlerConstants.DOMAINS_CRAWL_MAX_COUNT - domainActors.size();
                /* Start an actor for each domain, if not already started */
            for (final Domain domain : response.getCrawlableDomains()) {
                    /* Is this domain stopped? */
                if (unresponsiveDomains.containsKey(domain.getName())) {
                    LOG.info("Unresponsive domain: " + domain.getName() + ". This domain will not be processed.");
                    continue;
                }
                if (exhaustedDomains.containsKey(domain.getName())) {
                    LOG.info("Exhausted domain: " + domain.getName() + ". This domain will not be processed.");
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
            String domainName = nextLink.getNextLinkRequest().getDomain().getName();
            if (domainActors.get(domainName) != null) {
                domainActors.get(domainName).tell(nextLink, getSelf());
            }
        } else  if (message instanceof DownloadUrlRequest) {
            getParent().tell(message, getSelf());
        } else  if (message instanceof DownloadUrlResponse) {
            DownloadUrlResponse downloadUrlResponse = (DownloadUrlResponse) message;
            ActorRef domainActor = domainActors.get(downloadUrlResponse.getDownloadUrlRequest().getDomain().getName());
            if (domainActor != null) {
                domainActor.tell(message, getSelf());
            }
        } else  if (message instanceof DomainStartedMessage) {
            DomainStartedMessage domainStarted = (DomainStartedMessage) message;
            String domainName = domainStarted.getDomain().getName();
            domainActors.put(domainName, getSender());
        } else  if (message instanceof DomainStoppedMessage) {
            DomainStoppedMessage domainStopped = (DomainStoppedMessage) message;
            String domainName = domainStopped.getDomain().getName();
            domainActors.remove(domainName);
            /* Tell it to the statistics */
            getParent().tell(new AddDomainStatisticsRequest(domainStopped.getDomain()), getSelf());
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
        final ActorRef domainActor = getContext().actorOf(Props.create(DomainActor.class, getSelf(), domain),
                WebCrawlerConstants.DOMAIN_ACTOR_PART_NAME + getActorName(domain.getName()));

        LOG.info("Domain " + domain.getName() + " starting actor " + domainActor);

        domainActor.tell(new CrawlDomainRequest(domain), getSelf());

        getParent().tell(new AddDomainStatisticsRequest(domain), getSelf());
    }


    private final SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(1, TimeUnit.MINUTES),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override
                public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    if (throwable instanceof UnresponsiveDomainException) {
                        UnresponsiveDomainException unresponsiveDomainException = (UnresponsiveDomainException) throwable;
                        Domain domain = unresponsiveDomainException.getDomain();
                        String domainName = unresponsiveDomainException.getDomain().getName();
                        if (!unresponsiveDomains.containsKey(domainName)) {
                            unresponsiveDomains.put(domainName, Calendar.getInstance().getTimeInMillis());
                        }
                        Domain unresponsiveDomain = new Domain(domainName, domain.getCoolDownPeriod(), domain.getCrawledAt(), DomainStatus.UNRESPONSIVE);
                        /* Mark the domain as unresponsive */
                        getParent().tell(new UpdateDomainRequest(unresponsiveDomain), getSelf());
                        /* Tell it to the statistics */
                        getParent().tell(new AddDomainStatisticsRequest(unresponsiveDomain), getSelf());
                        /* Stop the actor */
                        return SupervisorStrategy.stop();
                    }

                    if (throwable instanceof ExhaustedDomainException) {
                        ExhaustedDomainException exhaustedDomainException = (ExhaustedDomainException) throwable;
                        Domain domain = exhaustedDomainException.getDomain();
                        String domainName = domain.getName();
                        if (!exhaustedDomains.containsKey(domainName)) {
                            exhaustedDomains.put(domainName, Calendar.getInstance().getTimeInMillis());
                        }
                        Domain exhaustedDomain = new Domain(domainName, domain.getCoolDownPeriod(), domain.getCrawledAt(), DomainStatus.EXHAUSTED);
                        /* Mark the domain as exhausted */
                        getParent().tell(new UpdateDomainRequest(exhaustedDomain), getSelf());
                        /* Tell it to the statistics */
                        getParent().tell(new AddDomainStatisticsRequest(exhaustedDomain), getSelf());
                        /* Stop the actor */
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