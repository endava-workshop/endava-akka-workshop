package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.UntypedActor;
import com.en_workshop.webcrawlerakka.akka.requests.ProcessContentRequest;
import org.apache.log4j.Logger;

/**
 * Processing master actor
 *
 * @author Radu Ciumag
 */
public class ProcessingMasterActor extends UntypedActor {
    private static final Logger LOG = Logger.getLogger(ProcessingMasterActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ProcessContentRequest) {
            LOG.info("ProcessContentRequest: " + message);

            //TODO

            LOG.info("ProcessContentRequest: DONE");
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}