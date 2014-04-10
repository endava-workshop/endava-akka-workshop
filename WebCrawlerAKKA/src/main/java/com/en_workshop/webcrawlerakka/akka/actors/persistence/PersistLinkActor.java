package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.PersistLinkRequest;
import com.en_workshop.webcrawlerakka.dao.LinkDao;

/**
 * Actor that will persist the links.
 * <p/>
 * Created by roxana on 3/13/14.
 */
public class PersistLinkActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof PersistLinkRequest) {
            PersistLinkRequest persistLinkRequest = (PersistLinkRequest) message;
            LOG.info("Received link to persist: " + persistLinkRequest.getLink().getUrl());

            // TODO Remove after the persistence infrastructure is up and running
            LinkDao.update(persistLinkRequest.getLink());
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }

    }
}
