package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import org.apache.log4j.Logger;

/**
 * Persistence master actor
 *
 * @author Radu Ciumag
 */
public class PersistenceMasterActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(PersistenceMasterActor.class);

    private ActorRef listDomainsRouter;
    private ActorRef nextLinkRouter;

    /**
     * The default constructor
     */
    public PersistenceMasterActor() {
        this.listDomainsRouter = getContext().actorOf(Props.create(ListDomainsActor.class).withRouter(new FromConfig()), "listDomainsRouter");
        this.nextLinkRouter = getContext().actorOf(Props.create(NextLinkActor.class).withRouter(new FromConfig()), "nextLinkRouter");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ListDomainsRequest) {
            listDomainsRouter.tell(message, getSender());
        } else if (message instanceof NextLinkRequest) {
            nextLinkRouter.tell(message, getSender());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}