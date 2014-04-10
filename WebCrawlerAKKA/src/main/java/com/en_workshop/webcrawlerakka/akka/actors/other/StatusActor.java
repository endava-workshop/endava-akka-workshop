package com.en_workshop.webcrawlerakka.akka.actors.other;

import akka.actor.ActorNotFound;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.other.status.StatusMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.status.StatusMasterResponse;
import com.en_workshop.webcrawlerakka.enums.ActorStatus;

/**
 * Status for actor
 *
 * @author Radu Ciumag
 */
public class StatusActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof StatusMasterRequest) {
            /* Query the status of MasterActor */
            try {
                findLocalActor(null);

                getSender().tell(new StatusMasterResponse((StatusMasterRequest) message, ActorStatus.WORKING, "MasterActor found"), getSelf());
            } catch (ActorNotFound exc) {
                getSender().tell(new StatusMasterResponse((StatusMasterRequest) message, ActorStatus.STOPPED, "MasterActor not found"), getSelf());
            }
        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }
}