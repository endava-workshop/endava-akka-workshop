package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.Function;
import akka.routing.FromConfig;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.ListDomainsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.NextLinkRequest;
import org.apache.log4j.Logger;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Persistence master actor
 *
 * @author Radu Ciumag
 */
public class PersistenceMasterActor extends BaseActor {
    private static final Logger LOG = Logger.getLogger(PersistenceMasterActor.class);

    private final ActorRef listDomainsRouter;
    private final ActorRef nextLinkRouter;

    /**
     * The default constructor
     */
    public PersistenceMasterActor() {
        final SupervisorStrategy routersSupervisorStrategy = new OneForOneStrategy(2, Duration.create(1, TimeUnit.MINUTES),
                new Function<Throwable, SupervisorStrategy.Directive>() {
                    @Override
                    public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {
                        if (throwable instanceof Exception) {
                            return SupervisorStrategy.restart();
                        }

                        return SupervisorStrategy.stop();
                    }
                });

        this.listDomainsRouter = getContext().actorOf(Props.create(ListDomainsActor.class).withRouter(new FromConfig().withSupervisorStrategy(routersSupervisorStrategy)),
                "listDomainsRouter");
        this.nextLinkRouter = getContext().actorOf(Props.create(NextLinkActor.class).withRouter(new FromConfig().withSupervisorStrategy(routersSupervisorStrategy)),
                "nextLinkRouter");
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