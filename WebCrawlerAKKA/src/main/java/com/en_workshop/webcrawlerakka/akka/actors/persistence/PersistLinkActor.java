package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistLinkRequest;
import org.apache.log4j.Logger;

/**
 * Actor that will persist the links.
 *
 * Created by roxana on 3/13/14.
 */
public class PersistLinkActor extends BaseActor {

    private static final Logger LOG = Logger.getLogger(PersistLinkActor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof PersistLinkRequest) {

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }
}
