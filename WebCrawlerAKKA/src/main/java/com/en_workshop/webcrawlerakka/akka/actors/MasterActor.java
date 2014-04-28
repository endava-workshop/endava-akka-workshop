package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.routing.FromConfig;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.domain.DomainMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.domain.DownloadUrlActor;
import com.en_workshop.webcrawlerakka.akka.actors.persistence.PersistenceMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.processing.ProcessingMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.statistics.StatisticsActor;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.DownloadUrlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.domain.RefreshDomainMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsResponse;
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

    private final ActorRef parent;

    private ActorRef statistics;
    private ActorRef domainMasterActor;
    private ActorRef persistenceMaster;
    private ActorRef processingMaster;
    private ActorRef downloadUrlsRouter;

    public MasterActor(ActorRef parent) {
        this.parent = parent;
    }

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

    private final SupervisorStrategy downloadUrlsRouterStrategy = new OneForOneStrategy(-1, Duration.create(1, TimeUnit.MINUTES),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                @Override
                public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                    LOG.error("Exception in MasterActor routersSupervisorStrategy: type [" + throwable.getClass() + "], message [" + throwable.getMessage() + "]. DownloadUrlActor will restart.");
                    return SupervisorStrategy.restart();

                }
            }
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof StartMasterRequest) {
            /* Start the persistence actor */
            persistenceMaster = getContext().actorOf(Props.create(PersistenceMasterActor.class), WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME);
            LOG.debug("Started Persistence Master...");

            /* Start the processing actor */
            processingMaster = getContext().actorOf(Props.create(ProcessingMasterActor.class, getSelf()), WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME);
            LOG.debug("Started Processing Master...");

             /* Start the statistics actor */
            statistics = getContext().actorOf(Props.create(StatisticsActor.class), WebCrawlerConstants.STATISTICS_ACTOR_NAME);
            LOG.debug("Started Statistics actor...");

            /* Start the download URL router */
            this.downloadUrlsRouter = getContext().actorOf(Props.create(DownloadUrlActor.class).withRouter(
                    new FromConfig().withSupervisorStrategy(downloadUrlsRouterStrategy)),
                    "downloadUrlRouter");

            /* Start the domain master actor */
            domainMasterActor = getContext().actorOf(Props.create(DomainMasterActor.class, getSelf()), WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME);
            domainMasterActor.tell(new RefreshDomainMasterRequest(), getSelf());
            LOG.debug("Started Domain Master...");

        } else if (message instanceof DownloadUrlRequest) {
            downloadUrlsRouter.tell(message, getSelf());
        }  else if (message instanceof DownloadUrlResponse) {
            domainMasterActor.tell(message, getSelf());
        }  else if (message instanceof StatisticsRequest) {
            statistics.tell(message, getSelf());
        } else if (message instanceof ProcessingRequest) {
            processingMaster.tell(message, getSelf());
        } else if (message instanceof PersistenceRequest) {
            persistenceMaster.tell(message, getSelf());
        } else if (message instanceof ListCrawlableDomainsResponse) {
            domainMasterActor.tell(message, getSelf());
        } else if (message instanceof NextLinkResponse) {
            domainMasterActor.tell(message, getSelf());
        } else if (message instanceof ShowStatisticsResponse) {
            getParent().tell(message, getSelf());
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

    public ActorRef getParent() {
        return parent;
    }
}