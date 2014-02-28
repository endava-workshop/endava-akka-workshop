package com.en_workshop.webcrawlerakka.akka.actors.processing;

import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.processing.ProcessContentRequest;
import org.apache.log4j.Logger;

/**
 * Processing master actor
 *
 * @author Radu Ciumag
 */
public class ProcessingMasterActor extends BaseActor {
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