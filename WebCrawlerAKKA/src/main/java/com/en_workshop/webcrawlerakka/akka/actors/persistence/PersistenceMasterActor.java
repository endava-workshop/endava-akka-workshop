package com.en_workshop.webcrawlerakka.akka.actors.persistence;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.routing.FromConfig;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.requests.persistence.*;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Persistence master actor
 *
 * @author Radu Ciumag
 */
public class PersistenceMasterActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private final ActorRef persistenceRouter;

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

        this.persistenceRouter = getContext().actorOf(Props.create(PersistenceActor.class).withRouter(new FromConfig().withSupervisorStrategy(routersSupervisorStrategy)),
                "persistenceRouter");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof PersistenceRequest) {
            persistenceRouter.tell(message, getSender());
        } else {
            LOG.error("Unknown message: " + message);
        }
    }
}