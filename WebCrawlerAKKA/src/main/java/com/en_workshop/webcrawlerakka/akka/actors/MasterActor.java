package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.requests.StartDomainMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.StartProcessingMasterRequest;
import org.apache.log4j.Logger;

/**
 * Crawler root actor
 *
 * @author Radu Ciumag
 */
public class MasterActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(MasterActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof StartMasterRequest) {
            LOG.info("StartMasterRequest: " + message);

            /* Start the domain master actor */
            ActorRef domainMasterActor = getContext().actorOf(Props.create(DomainMasterActor.class), WebCrawlerConstants.DOMAIN_MASTER_ACTOR_NAME);
            domainMasterActor.tell(new StartDomainMasterRequest(), getSelf());

            /* Start the domain processing actor */
            ActorRef processingMasterActor = getContext().actorOf(Props.create(ProcessingMasterActor.class), WebCrawlerConstants.PROCESSING_MASTER_ACTOR_NAME);
            processingMasterActor.tell(new StartProcessingMasterRequest(), getSelf());

            LOG.info("StartMasterRequest: DONE");
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}