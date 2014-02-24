package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.domain.DomainMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.persistence.PersistenceMasterActor;
import com.en_workshop.webcrawlerakka.akka.actors.processing.ProcessingMasterActor;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.domain.RefreshDomainMasterRequest;
import org.apache.log4j.Logger;

/**
 * Crawler root actor
 *
 * @author Radu Ciumag
 */
public class MasterActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(MasterActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof StartMasterRequest) {
            /* Start the domain master actor */
            ActorRef domainMasterActor = getContext().actorOf(Props.create(DomainMasterActor.class), WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME);
            domainMasterActor.tell(new RefreshDomainMasterRequest(), getSelf());

            LOG.debug("Started Domain Master...");

            /* Start the processing actor */
            ActorRef processingMasterActor = getContext().actorOf(Props.create(ProcessingMasterActor.class), WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME);

            LOG.debug("Started Processing Master...");

            /* Start the persistence actor */
            ActorRef persistenceMasterActor = getContext().actorOf(Props.create(PersistenceMasterActor.class), WebCrawlerConstants.PERSISTENCE_MASTER_ACTOR_NAME);

            LOG.debug("Started Persistence Master...");
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}