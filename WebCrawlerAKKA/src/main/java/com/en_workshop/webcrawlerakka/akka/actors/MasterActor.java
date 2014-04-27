package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.domain.DomainMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.persistence.PersistenceMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.processing.ProcessingMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.statistics.StatisticsActor;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.RefreshDomainMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.*;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessingRequest;
import com.en_workshop.webcrawlerakka.akka.requests.statistics.StatisticsRequest;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Crawler root actor
 *
 * @author Radu Ciumag
 */
public class MasterActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private ActorRef statistics;
    private ActorRef domainMasterActor;
    private ActorRef persistenceMaster;
    private ActorRef processingMaster;

    private final SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.create(1, TimeUnit.MINUTES),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override
                public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    if (throwable instanceof Exception) {
                        LOG.error("Exception in MasterActor : type [" + throwable.getClass() + "], message [" + throwable.getMessage() + ". Will restart.");
                        return SupervisorStrategy.restart();
                    }
                    LOG.error("Exception in MasterActor : type [" + throwable.getClass() + "], message [" + throwable.getMessage() + ". Will stop.");
                    return SupervisorStrategy.stop();
                }
            });

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof StartMasterRequest) {
            /* Start the domain master actor */
            domainMasterActor = getContext().actorOf(Props.create(DomainMasterActor.class, getSelf()), WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME);
            domainMasterActor.tell(new RefreshDomainMasterRequest(), getSelf());
            LOG.debug("Started Domain Master...");

            /* Start the persistence actor */
            persistenceMaster = getContext().actorOf(Props.create(PersistenceMasterActor.class), WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME);
            LOG.debug("Started Persistence Master...");

            /* Start the processing actor */
            processingMaster = getContext().actorOf(Props.create(ProcessingMasterActor.class, getSelf()), WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME);
            LOG.debug("Started Processing Master...");

             /* Start the statistics actor */
            statistics = getContext().actorOf(Props.create(StatisticsActor.class), WebCrawlerConstants.STATISTICS_ACTOR_NAME);
            LOG.debug("Started Statistics actor...");

        } else if (message instanceof StatisticsRequest) {
            statistics.tell(message, getSelf());
        } else if (message instanceof ProcessingRequest) {
            processingMaster.tell(message, getSelf());
        } else if (message instanceof PersistenceRequest) {
            persistenceMaster.tell(message, getSelf());
        } else if (message instanceof ListDomainsResponse) {
            domainMasterActor.tell(message, getSelf());
        } else if (message instanceof NextLinkResponse) {
            domainMasterActor.tell(message, getSelf());
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