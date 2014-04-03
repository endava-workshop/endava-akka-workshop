package com.en_workshop.webcrawlerakka.akka.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Base actor
 *
 * @author Radu Ciumag
 */
public abstract class BaseActor extends UntypedActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * Find an local actor specified by name and do some work with it
     *
     * @param partialName The partial actor name
     * @param onSuccess   On success action
     * @param onFailure   On failure action
     */
    protected void findLocalActor(final String partialName, final OnSuccess<ActorRef> onSuccess, final OnFailure onFailure) {
        final Future<ActorRef> actorRef = getContext().actorSelection("akka://" + WebCrawlerConstants.SYSTEM_NAME + "/user/" + WebCrawlerConstants.MASTER_ACTOR_NAME + "/" +
                partialName).resolveOne(Duration.create(1, TimeUnit.SECONDS));

        actorRef.onSuccess(onSuccess, getContext().dispatcher());
        actorRef.onFailure(onFailure, getContext().dispatcher());
    }

    /**
     * Find an remote actor specified by name and do some work with it
     *
     * @param pathFromUser The partial path from: /user/[pathFromUser]
     * @param onSuccess    On success action
     * @param onFailure    On failure action
     */
    protected void findRemoteActor(final String remoteSystemIdentification, final String pathFromUser, final OnSuccess<ActorRef> onSuccess, final OnFailure onFailure) {
        final Future<ActorRef> actorRef = getContext().actorSelection("akka.tcp://" + remoteSystemIdentification + "/user/" + pathFromUser).resolveOne(
                Duration.create(5, TimeUnit.SECONDS));

        actorRef.onSuccess(onSuccess, getContext().dispatcher());
        actorRef.onFailure(onFailure, getContext().dispatcher());
    }

    /**
     * Process the string and convert it to a valid actor name
     *
     * @param name The original name
     * @return The valid actor name
     */
    protected String getActorName(String name) {
        return name.replace('.', '_');
    }
}